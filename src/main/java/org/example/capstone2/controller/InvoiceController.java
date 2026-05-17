package org.example.capstone2.controller;

import lombok.RequiredArgsConstructor;
import org.example.capstone2.model.MaintenanceRequest;
import org.example.capstone2.service.InvoicePdfService;
import org.example.capstone2.service.MaintenanceRequestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoicePdfService          invoicePdfService;
    private final MaintenanceRequestService  requestService;


    @GetMapping("/download-invoce/{userid}/{requestId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable  Integer userid,@PathVariable  Integer requestId) {

        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(userid, requestId);

        String filename = String.format("invoice-INV-%05d.pdf", requestId);

        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdfBytes.length))
                .body(pdfBytes);
    }
}