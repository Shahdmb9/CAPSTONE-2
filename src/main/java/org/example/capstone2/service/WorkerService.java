package org.example.capstone2.service;


import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.*;
import org.example.capstone2.repository.*;
import org.springframework.stereotype.Service;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final MaintenanceRequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final WhatsAppService whatsAppService;
    private final MaterialRepository materialRepository;
    private final NotificationService notificationService;
    private final OpenAiService openAiService;
    private final DistanceMatrixService distanceMatrixService;


    //register a new worker

    public void add(Worker worker) {
        Category category = categoryRepository.findCategoryById(worker.getSpecialtyAt());
        if (category == null) {
            throw new ApiException("Category not found: " );
        }
        worker.setAvailable(true);
        workerRepository.save(worker);
    }

    //get all workers

    public List<Worker> getAll() {
        return workerRepository.findAll();
    }

    public void deleteWorker(Integer workerId) {
        Worker worker=getWorkerById(workerId);
        workerRepository.delete(worker);
    }

    public Worker update(Integer workerId, Worker worker) {
        Worker oldWorker = getWorkerById(workerId);
        if (worker == null)
            throw new ApiException("Worker not found: " + workerId);
        oldWorker.setName(worker.getName());
        oldWorker.setPhone(worker.getPhone());
        oldWorker.setBaseSalary(worker.getBaseSalary());
        oldWorker.setSpecialtyAt(worker.getSpecialtyAt());
        oldWorker.setAvailable(worker.isAvailable());
        return workerRepository.save(oldWorker);
    }

    // get worker by ID

    public Worker getWorkerById(Integer workerId) {
        Worker worker=workerRepository.findWorkerById(workerId);
        if(worker == null)
            throw new ApiException("Worker not found: " );
        return worker;
    }

    // get available workers

    public List<Worker> getAvailableWorkers() {
        if(workerRepository.findByAvailable(true).isEmpty())
            throw new ApiException("No workers found");
        return workerRepository.findByAvailable(true);
    }

    public List<Worker> getBestWorkers() {
        if(workerRepository.findBestWorkers().isEmpty())
            throw new ApiException("No workers has been rated yet");
        return workerRepository.findBestWorkers();
    }



    public List<Worker> getWorkersBySpecialty(Integer specialtyAt) {
        if(workerRepository.findWorkerBySpecialtyAt(specialtyAt).isEmpty())
            throw new ApiException("No workers found in this category " );

        return workerRepository.findWorkerBySpecialtyAt(specialtyAt);
    }

    public List<Worker> getWorkersByPriceRange(Integer min,Integer max) {
        if(min>max)
            throw new ApiException("Min price must be less than max price");
        if(workerRepository.wokersWithBasePriceRange(min,max).isEmpty())
            throw new ApiException("No workers found in this price range");
        return workerRepository.wokersWithBasePriceRange(min,max);
    }

    public List<Worker> findBestWorkerInSpeciality(Integer specialityid){
        Category category=categoryRepository.findCategoryById(specialityid);
        if(category==null)
            throw new ApiException("Category not found: ");
        List<Worker> workers=workerRepository.findBestWorkersBySpeciality(specialityid);
        if(workers.isEmpty())
            throw new ApiException("No workers found in this category");
        return workers;
    }


    public List<Worker> getAvailableWorkersBySpecialty(Integer specialtyAt) {
        if(workerRepository.findWorkerByAvailableAndSpecialtyAt(true, specialtyAt).isEmpty())
            throw new ApiException("No workers found in this category " );
        return workerRepository.findWorkerByAvailableAndSpecialtyAt(true, specialtyAt);
    }

    public List<Worker> sortWorkersByPriceLowToHigh() {
        if(workerRepository.sortWorkersByPriceLowToHigh().isEmpty())
            throw new ApiException("No workers found");
        return workerRepository.sortWorkersByPriceLowToHigh();
    }

    public List<Worker> sortWorkersByPriceHighToLow() {
        if(workerRepository.sortWorkersByPriceHighToLow().isEmpty())
            throw new ApiException("No workers found");
        return workerRepository.sortWorkersByPriceHighToLow();
    }

    public List<Worker> sortWorkersByPriceHighToLowWithSpecialty(Integer specialty) {
        if(workerRepository.sortWorkersByPriceHighToLowWithSpecialty(specialty).isEmpty())
            throw new ApiException("No workers found");
        return workerRepository.sortWorkersByPriceHighToLowWithSpecialty(specialty);
    }

    public List<Worker> sortWorkersByPriceLowToHighWithSpecialty(Integer specialty) {
        if(workerRepository.sortWorkersByPriceLowToHighWithSpecialty(specialty).isEmpty())
            throw new ApiException("No workers found");
        return workerRepository.sortWorkersByPriceLowToHighWithSpecialty(specialty);
    }



    //accept request

    public void acceptRequest(Integer workerId, Integer requestId) {
        Worker worker = getWorkerById(workerId);
        MaintenanceRequest request = requestRepository.findMaintenanceRequestById(requestId);

        if(!worker.getId().equals(request.getWorkerId())&&request.getWorkerId()!=null)
            throw new RuntimeException("This request is for another worker");

        if(request==null)
            throw new ApiException("Request not found: ");

        if (!request.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Request is on" + request.getStatus());
        }
        if(request.getCategoryId()!=worker.getSpecialtyAt())
            throw new RuntimeException("Worker specialty does not match request needed");

        if (!worker.isAvailable()) {
            throw new RuntimeException("Worker is not available to take new requests");
        }


        worker.setAvailable(false);
        workerRepository.save(worker);
        request.setWorkerId(workerId);
        request.setStatus("ASSIGNED");

        //setting worker id for all the requests that are waiting for this worker to null and notify users he is not available
        List<MaintenanceRequest> updatedRequest=requestRepository.findMaintenanceRequestByWorkerIdAndStatus(workerId,"PENDING");
        requestRepository.updateAllWorkerIdInRequest(workerId);
        //uncomment later
        notificationService.notifyUsersNotAvailably(updatedRequest,workerId);
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    public void rejectRequest(Integer workerId, Integer requestId) {

        Worker worker = getWorkerById(workerId);
        MaintenanceRequest request = requestRepository.findMaintenanceRequestById(requestId);
        User user = userRepository.findUserById(request.getUserId());

        if(!worker.getId().equals(request.getWorkerId()))
            throw new RuntimeException("This request is for another worker");

        if(request==null)
            throw new ApiException("Request not found: ");

        if (!request.getStatus().equalsIgnoreCase("PENDING")) {
            throw new RuntimeException("Request is on" + request.getStatus());
        }

        request.setWorkerId(null);
        request.setUpdatedAt(LocalDateTime.now());
        //uncomment later
       // whatsAppService.sendChatMessage(user.getPhone(),"You're request to worker "+worker.getName()+" Got rejected");
        requestRepository.save(request);

    }

    //start working on a request

    public void startRequest(Integer workerId, Integer requestId) {
        MaintenanceRequest request = getMyRequest(workerId, requestId);
        if(request == null)
            throw new ApiException("Request not found: ");

        Worker worker=getWorkerById(workerId);

        if(!worker.getId().equals(request.getWorkerId()))
            throw new RuntimeException("This request is for another worker");

        if (!request.getStatus().equalsIgnoreCase("ASSIGNED")) {
            throw new RuntimeException("Only ASSIGNED requests can be started");
        }
        request.setStatus("IN_PROGRESS");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    //resolve a request

    public void resolveRequest(Integer workerId, Integer requestId) {
        Worker worker = getWorkerById(workerId);

        MaintenanceRequest request = getMyRequest(workerId, requestId);
        if (!request.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
            throw new RuntimeException("Only IN_PROGRESS requests can be resolved");
        }
        if(worker.getId()!=request.getWorkerId())
            throw new RuntimeException("This request is not assigned to you");

        worker.setAvailable(true);
        workerRepository.save(worker);
        //uncomment later
        notificationService.notifyUsers(workerId);
        request.setStatus("RESOLVED");
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
    }

    //View all my assigned requests

    public List<MaintenanceRequest> getWorkerRequests(Integer workerId) {
        getWorkerById(workerId); // //checking if the worker exists
        return requestRepository.findMaintenanceRequestByWorkerId(workerId);
    }

    //View my assigned requests filtered by status
    public List<MaintenanceRequest> getWorkerRequestsByStatus(Integer workerId, String status) {
        getWorkerById(workerId);//checking if the worker exists
        return requestRepository.findMaintenanceRequestByWorkerIdAndStatus(workerId, status.toUpperCase());
    }


    public Map<String, Object> getTotalEarningsThisMonth(Integer workerId) {
        getWorkerById(workerId);
        List<MaintenanceRequest> maintenanceRequest=requestRepository.findMaintenanceRequestByWorkerId(workerId);
        if(maintenanceRequest.isEmpty())
            throw new ApiException("you dont have requests");
        Double salary    = requestRepository.getTotalSalaryThisMonth(workerId);
        Double materials = materialRepository.getTotalMaterialsCostThisMonth(workerId);

        Map<String, Object> result = new HashMap<>();
        result.put("salaryEarned",salary);
        result.put("materialsEarned", materials);
        result.put("totalEarned",salary + materials);
        return result;
    }
    public Map<String, Object> getTotalEarningsMonthRange(Integer workerId, LocalDate startDate, LocalDate endDate) {
        getWorkerById(workerId);
        List<MaintenanceRequest> maintenanceRequest=requestRepository.findMaintenanceRequestByWorkerId(workerId);
        if(maintenanceRequest.isEmpty())
            throw new ApiException("you dont have requests");

        Double salary    = requestRepository.getTotalSalaryBetweenMonths(workerId,startDate,endDate);
        Double materials = materialRepository.getTotalMaterialsCostBetweenMonths(workerId,startDate,endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("salaryEarned",salary);
        result.put("materialsEarned", materials);
        result.put("totalEarned",salary + materials);
        return result;
    }

    //sort workers by rating in specific specialty

    public List<Worker> sortWorkersByRatingAndSpicilityAndAvailabilty(Integer categoryId) {
        Category category = categoryRepository.findCategoryById(categoryId);
        if (category == null) {
            throw new ApiException("Category not found: " );
        }
        return workerRepository.findBestWorkersBySpecialityAndAvailable(categoryId);
    }

    //get distance between worker and user
    public Map<String, Object> getDistanceBetweenWorkersAndUser(Integer workerId, Integer requestId) {
        Worker worker = getWorkerById(workerId);

        MaintenanceRequest request = requestRepository.findMaintenanceRequestById(requestId);
        if(request==null)
            throw new ApiException("Request not found: ");
        if (request.getWorkerId() != null && !request.getWorkerId().equals(workerId)) {
            throw new RuntimeException("this request is not assigned to you");
        }

        User user = userRepository.findUserById(request.getUserId());

        Map<String, Object> result = new HashMap<>();

        Double distance=distanceMatrixService.getDistance(worker.getDistrict(),user.getDistrict());

        result.put("distance",distance+" km");

        return result;
    }

    //Get worker stats



    public void updateWorkerAvailability(Integer workerId) {
        Worker worker = getWorkerById(workerId);
        worker.setAvailable(!worker.isAvailable());
        workerRepository.save(worker);
        //uncomment later
        if(worker.isAvailable())
            notificationService.notifyUsers(workerId);
    }

    public List<MaintenanceRequest> getWorkerUrgentRequest(Integer workerId){
        getWorkerById(workerId);
        return requestRepository.findMaintenanceRequestByWorkerIdAndUrgentIsTrue(workerId);
    }

    //letting the AI estmate the time to solve the problem based on the problem description

    public Map<String, Object> getRequiestEstmatedTime(Integer workerId,Integer requestId ) {

        MaintenanceRequest request = requestRepository.findMaintenanceRequestById(requestId);
        if(request==null)
            throw new ApiException("Request not found: ");
        if (request.getWorkerId() != null && !request.getWorkerId().equals(workerId)) {
            throw new RuntimeException("this request is not assigned to you");
        }

        String promot="based on this maintenance problem:"+request.getDescription()+"\n" +
                "give me the estimated time to solve it as a specialist in the problem field \n" +
                "answer as json including hours and time ex: {\"hours\" : 1,\n" +
                "\"minutes\" : 30} dont add anything else. hours let it numbers only without dot, and the minute also   ";

        //convert the response string as json object
        JSONObject jsonObject = new JSONObject(openAiService.estmatedTime(promot));
        Integer hours = jsonObject.getInt("hours");
        Double minutes = jsonObject.getDouble("minutes");

        Map<String, Object> result = new HashMap<>();
        result.put("hours", hours);
        result.put("minutes", minutes);
        return result;

    }

    public Map<String, Double> getWorkerRating(Integer workerId) {
        Worker worker=getWorkerById(workerId);
        Double avgRating = ratingRepository.getAverageScoreByWorkerId(workerId);
        Map<String, Double> result = new HashMap<>();
        result.put("rating", avgRating != null ? avgRating : 0.0);
        return result;
    }

    public Map<String, Object> getWorkerStats(Integer workerId) {
        getWorkerById(workerId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", requestRepository.countByWorkerId(workerId));
        stats.put("resolved", requestRepository.countMaintenanceRequestByWorkerIdAndStatus(workerId, "RESOLVED"));
        stats.put("inProgress", requestRepository.countMaintenanceRequestByWorkerIdAndStatus(workerId, "IN_PROGRESS"));
        stats.put("assigned", requestRepository.countMaintenanceRequestByWorkerIdAndStatus(workerId, "ASSIGNED"));

        Double avgRating = ratingRepository.getAverageScoreByWorkerId(workerId);
        stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0);

        return stats;
    }

    // helper method: get request and verify it belongs to this worker

    private MaintenanceRequest getMyRequest(Integer workerId, Integer requestId) {
        MaintenanceRequest request = requestRepository.findMaintenanceRequestById(requestId);
        if(request==null)
            throw new ApiException("Request not found: ");
        if (request.getWorkerId() == null || !request.getWorkerId().equals(workerId)) {
            throw new RuntimeException("this request is not assigned to you");
        }
        return request;
    }


}

