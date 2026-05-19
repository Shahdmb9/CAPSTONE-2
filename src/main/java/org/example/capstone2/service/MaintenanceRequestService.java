package org.example.capstone2.service;


import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.*;
import org.example.capstone2.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final WorkerRepository workerRepository;
    private final MaterialRepository materialRepository;
    private final MailService mailService;
    private final WhatsAppService whatsAppService;
    private final OpenAiService openAiService;
    private final DistanceMatrixService distanceMatrixService;



    // ── CRUD ──────────────────────────────────────────────────

    public List<MaintenanceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public void submitRequest(MaintenanceRequest request) {
        User user= userRepository.findUserById(request.getUserId());

        if(user==null)
                throw new ApiException("User not found: " + request.getUserId());


        //using AI to classify the request
        Integer categorey=openAiService.chat(request.getTitle());
        request.setCategoryId(categorey);
        //check if the category exists
        Category category = categoryRepository.findCategoryById(request.getCategoryId());
        if(category==null)
            throw new ApiException("We can not classify: Make sure your is specific");

        if(request.getWorkerId()!=null) {
            if(user.getSubscriptionType().equalsIgnoreCase("FREE"))
                throw new ApiException("Only PREMIUM users can assign workers");

           Worker worker =workerRepository.findWorkerById(request.getWorkerId());

           if(worker==null)
               throw new ApiException("Worker not found:");
           if(!worker.isAvailable())
               throw new ApiException("Worker is not available ");

           if(worker.getSpecialtyAt()!=request.getCategoryId())
               throw new ApiException("Worker specialty does not match request needed");
            //uncomment later
           whatsAppService.sendChatMessage(worker.getPhone(),"You have new request: "+request.getTitle()+"\n"+request.getDescription());

        }
        request.setStatus("PENDING");
        request.setUrgent(false);
        request.setCreatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    public void deleteRequest(Integer requestId) {
        getRequestById(requestId);
        requestRepository.deleteById(requestId);
    }

    //check if i have to send the residintid in the path varible
    public MaintenanceRequest updateRequest(Integer requestId,MaintenanceRequest request) {
        MaintenanceRequest oldRequest = getRequestById(requestId);
        if (!oldRequest.getUserId().equals(request.getUserId())) {
            throw new ApiException("this request does not belong to you");
        }

        if (!oldRequest.getStatus().equalsIgnoreCase("PENDING")) {
            throw new ApiException("Can not edit a request with status: " + request.getStatus());
        }
        oldRequest.setTitle(request.getTitle());
        oldRequest.setDescription(request.getDescription());
        oldRequest.setCategoryId(request.getCategoryId());
        oldRequest.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(oldRequest);
    }

    // extra

    public MaintenanceRequest getRequestById(Integer requestId) {
        MaintenanceRequest request=requestRepository.findMaintenanceRequestById(requestId);
        if(request == null)
               throw  new ApiException("Request not found: " + requestId);
        return request;
    }

    // Get all requests by status

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        if(requestRepository.findMaintenanceRequestByStatus(status.toUpperCase()).isEmpty())
            throw new ApiException("No requests found with status: " + status);
        return requestRepository.findMaintenanceRequestByStatus(status.toUpperCase());
    }

    //Get all requests by category

    public List<MaintenanceRequest> getRequestsByCategory(Integer categoryId) {
        if(categoryRepository.findCategoryById(categoryId)==null)
                throw  new RuntimeException("Category not found: ");
        return requestRepository.findMaintenanceRequestByCategoryId(categoryId);
    }

    //Get all requests that are urgent

    public List<MaintenanceRequest> getUrgentRequests() {
        if(requestRepository.findMaintenanceRequestByUrgentIsTrue().isEmpty())
            throw new ApiException("No urgent requests found");
        return requestRepository.findMaintenanceRequestByUrgentIsTrue();
    }

    //Cancel a request

    public void cancelRequest(Integer userId, Integer requestId) {
        MaintenanceRequest request = getRequestById(requestId);
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("this request does not belong to you");
        }
        //check if the request is not accepted yet
        if (!request.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Only PENDING requests can be cancelled");
        }
        request.setStatus("CANCELLED");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    //Assign a request to the best worker (only for premium users)

    public void autoAssignToBestWorker(Integer requestId) {
        MaintenanceRequest request = getRequestById(requestId);
        User user =userRepository.findUserById(request.getUserId());
        List<Worker> workers=workerRepository.findWorkerByAvailableAndSpecialtyAt(true,request.getCategoryId());
        if(user==null)
            throw new ApiException("User not found: ");

        if(user.getSubscriptionType().equalsIgnoreCase("FREE"))
            throw new ApiException("Only PREMIUM users can use auto-assign");

        if (!request.getStatus().equalsIgnoreCase("PENDING"))
            throw new ApiException("Only PENDING requests can be auto-assigned");

        if(workers.isEmpty())
            throw new ApiException("No available workers for this category");
        
        //get the best worker for this category
        Worker best=null;
        if(!workerRepository.findBestWorkersBySpecialityAndAvailable(request.getCategoryId()).isEmpty())
             best = workerRepository.findBestWorkersBySpecialityAndAvailable(request.getCategoryId()).get(0);
        
        //if no one has rating get any worker in this category
        if(best==null)
            best=workers.get(0);
//uncomment later
        whatsAppService.sendChatMessage(best.getPhone(),"You have new request: "+request.getTitle()+"\n"+request.getDescription());
        request.setWorkerId(best.getId());
//        request.setStatus("ASSIGNED");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    //assign a request to closer worker
    public void assignToCloserWorker(Integer requestId) {

        MaintenanceRequest request = getRequestById(requestId);

        User user =userRepository.findUserById(request.getUserId());
        List<Worker> workers=workerRepository.findWorkerByAvailableAndSpecialtyAt(true,request.getCategoryId());
        if(user==null)
            throw new ApiException("User not found: ");

        if(user.getSubscriptionType().equalsIgnoreCase("FREE"))
            throw new ApiException("Only PREMIUM users can use auto-assign");

        if (!request.getStatus().equalsIgnoreCase("PENDING"))
            throw new ApiException("Only PENDING requests can be assigned to the closest worker");

        if(workers.isEmpty())
            throw new ApiException("No available workers for this category");

        Worker closest=findClosest(user,workers);
        request.setWorkerId(closest.getId());
        whatsAppService.sendChatMessage(closest.getPhone(),"You have new request:");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);

    }



    //admin assigns worker to urgent request

    public void AdminAssiningWorker(Integer userid,Integer workerId,Integer requestId){
        User user = userRepository.findUserById(userid);
        if(user==null)
            throw new ApiException("User not found: ");

        //check if the user is admin

        if(!user.getRole().equalsIgnoreCase("ADMIN"))
            throw new ApiException("Only ADMIN can assign workers");

        MaintenanceRequest request=getRequestById(requestId);

        if(request==null)
            throw new ApiException("Request not found: ");

        if(request.getWorkerId()!=null)
            throw new ApiException("Worker already assigned");

        Worker worker=workerRepository.findWorkerById(workerId);
        if(worker==null)
            throw new ApiException("Worker not found: ");

        if(worker.getSpecialtyAt()!=request.getCategoryId())
            throw new ApiException("Worker specialty does not match request needed");

        if(!worker.isAvailable())
            throw new ApiException("Worker is not available");

        request.setWorkerId(workerId);
        requestRepository.save(request);
        //uncomment later
        whatsAppService.sendChatMessage(worker.getPhone(),"You have new request from the admin: "+request.getTitle()+"\n"+request.getDescription());

    }

    // calculate Total Cost of request
    public Map<String,Object> calculateTotalCost(Integer userid,Integer requestId){

        MaintenanceRequest request=getRequestById(requestId);

        //bring all the materials used in the request
        List<Material> material=materialRepository.findMaterialByRequestId(requestId);

        //bring the worker who work in the request
        Worker worker=workerRepository.findWorkerById(request.getWorkerId());

        if(!request.getStatus().equalsIgnoreCase("RESOLVED"))
            throw new ApiException("Only COMPLETED requests can be calculated");

        if(request==null)
            throw new ApiException("Request not found: ");

        if(request.getUserId()!=userid)
            throw new ApiException("User not authorized to view this invoice");


        if(worker==null)
            throw new ApiException("Worker not found: ");

        Double totalCost=0.0;

        //calculate materials cost
        if(!material.isEmpty())
            totalCost+=materialRepository.getTotalCostByRequestId(requestId);

        //adding worker base salary to total cost

        totalCost+=worker.getBaseSalary();
        Map<String, Object> result = new HashMap<>();

        //adding total cost to result hashmap to be json format
        result.put("Totalcost", totalCost);

        return result;

    }

    // tracking status of request
//
//    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
//    public void markUnacceptedRequestsAsUrgent() {
//            // Get all pending requests that are not yet marked as urgent
//            List<MaintenanceRequest> pendingRequests = requestRepository.findMaintenanceRequestByUrgentIsFalseAndStatus("PENDING");
//
//            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
//
//            for (MaintenanceRequest request : pendingRequests) {
//                if (request.getCreatedAt().isBefore(fiveMinutesAgo)) {
//                    User user = userRepository.findUserByRole("ADMIN");
//                    request.setUrgent(true);
//                    request.setUpdatedAt(LocalDateTime.now());
//                    requestRepository.save(request);
//                    //notifying the admin that the request is urgent and need assigning
//                    String message="Request ID: " + request.getId() + " marked as URGENT";
//                    mailService.sendPlainText(user.getEmail(),"Urgent request",message);
//
//                }
//            }
//
//    }

    public List<Worker> getClosetWorkersInCategory(Integer userid,Integer category) {
        List<Worker> workers=workerRepository.findBestWorkersBySpecialityAndAvailable(category);
        if(workers.isEmpty())
            throw new ApiException("No workers found in this category");
        User user=userRepository.findUserById(userid);
        if(user==null)
            throw new ApiException("User not found: ");

        Map<Integer,Double> sortByDistance=sortByDistance(workers,user);

        List<Worker> closetWorkers = new ArrayList<>();

        //counter becouse I want to get only 5 workers
        int counter=5;
        //loop through the returned sorted map
        for(Map.Entry<Integer,Double> entry:sortByDistance.entrySet()){
            //getting the Key witch stores the worker id
            Worker worker=workerRepository.findWorkerById(entry.getKey());
            if(worker!=null)
                closetWorkers.add(worker);
            counter--;
            if(counter==0)
                break;
        }
        return closetWorkers;

    }
    public Map<Integer,Double> sortByDistance(List<Worker> workers,User user){

        // 1. collect distance with worker id and save them as key value
        Map<Integer, Double> distanceMap = new LinkedHashMap<>();
        for (Worker worker : workers) {
            Double dist = distanceMatrixService.getDistance(worker.getDistrict(),user.getDistrict());
            distanceMap.put(worker.getId(), dist);
        }

        //make it as list to sort them
        List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(distanceMap.entrySet());

        // 3. Sort the list by value which contains distance
        Collections.sort(entryList, Comparator.comparingDouble(Map.Entry::getValue));

        // 4. saved the sorted list to a new map again
        Map<Integer, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    // global stats summary

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("urgent", requestRepository.countMaintenanceRequestByUrgentIsTrue());
        stats.put("pending", requestRepository.countByStatus("PENDING"));
        stats.put("assigned", requestRepository.countByStatus("ASSIGNED"));
        stats.put("inProgress", requestRepository.countByStatus("IN_PROGRESS"));
        stats.put("resolved", requestRepository.countByStatus("RESOLVED"));
        stats.put("cancelled", requestRepository.countByStatus("CANCELLED"));
        return stats;
    }

    //stats for categorey
    public Map<String, Object> getStatsOfCategory() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Plumbing", requestRepository.countMaintenanceRequestByCategoryId(1));
        stats.put("Electrical", requestRepository.countMaintenanceRequestByCategoryId(2));
        stats.put("HVAC", requestRepository.countMaintenanceRequestByCategoryId(3));
        stats.put("General", requestRepository.countMaintenanceRequestByCategoryId(4));

        return stats;
    }

    //helper
    public Worker findClosest(User user, List<Worker> workers) {
        Worker closestWorker = workers.get(0);
        Double minDist = distanceMatrixService.getDistance(user.getDistrict(), closestWorker.getDistrict());;

        for (Worker worker : workers) {
            Double distance = distanceMatrixService.getDistance(user.getDistrict(), worker.getDistrict()); // call API for each worker
            if (distance < minDist) {
                minDist = distance;
                closestWorker = worker;
            }
        }
        return closestWorker;
    }


}
