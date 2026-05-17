package org.example.capstone2.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.example.capstone2.ApiResponse.ApiException;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.model.Material;
import org.example.capstone2.model.Worker;
import org.example.capstone2.repository.MaintenanceRequestRepository;
import org.example.capstone2.repository.MaterialRepository;
import org.example.capstone2.repository.WorkerRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private final WorkerRepository   workerRepository;
    private final MaterialRepository materialRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public byte[] generateInvoicePdf(Integer userId, Integer requestId) {

        MaintenanceRequest request = maintenanceRequestRepository.findMaintenanceRequestById(requestId);

        // check if the request exists and belongs to this user
        if (request == null)
            throw new ApiException("Request not found");

        if (!request.getStatus().equalsIgnoreCase("RESOLVED"))
            throw new ApiException("Only RESOLVED requests can be invoiced");

        if (!request.getUserId().equals(userId))
            throw new ApiException("You are not allowed to view this invoice");

        // get the worker and materials for this request
        Worker worker    = workerRepository.findWorkerById(request.getWorkerId());
        List<Material> materials = materialRepository.findMaterialByRequestId(requestId);

        // calculate the total cost of the materials and add it to worker base salary'
        double materialsCost = 0;
        for (Material m : materials) {
            materialsCost += m.getQuantityUsed() * m.getUnitCost();
        }
        double totalCost = worker.getBaseSalary() + materialsCost;

        // build the PDF
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfDocument pdfDoc   = new PdfDocument(new PdfWriter(baos));
            Document    document = new Document(pdfDoc);

            // header
            document.add(new Paragraph("==================================="));
            document.add(new Paragraph("       MAINTENANCE INVOICE         "));
            document.add(new Paragraph("==================================="));
            document.add(new Paragraph(" "));

            // invoice info
            document.add(new Paragraph("Invoice #: INV-" + requestId));
            document.add(new Paragraph("Request  : " + request.getTitle()));
            document.add(new Paragraph("Status   : " + request.getStatus()));
            document.add(new Paragraph(" "));

            // worker info
            document.add(new Paragraph("-----------------------------------"));
            document.add(new Paragraph("Worker   : " + worker.getName()));
            document.add(new Paragraph("Phone    : " + worker.getPhone()));
            document.add(new Paragraph("Labour   : SAR " + worker.getBaseSalary()));
            document.add(new Paragraph("-----------------------------------"));
            document.add(new Paragraph(" "));

            // materials (only show this section if there are materials)
            if (!materials.isEmpty()) {
                document.add(new Paragraph("Materials used:"));
                for (Material m : materials) {
                    double subtotal = m.getQuantityUsed() * m.getUnitCost();
                    document.add(new Paragraph(
                            "  - " + m.getName() +
                                    "  x" + m.getQuantityUsed() +
                                    "  @ SAR " + m.getUnitCost() +
                                    "  = SAR " + subtotal
                    ));
                }
                document.add(new Paragraph("Materials Total: SAR " + materialsCost));
                document.add(new Paragraph(" "));
            }

            // total
            document.add(new Paragraph("==================================="));
            document.add(new Paragraph("TOTAL DUE: SAR " + totalCost));
            document.add(new Paragraph("==================================="));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for using our service!"));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new ApiException("Could not generate PDF: " + e.getMessage());
        }
    }
}