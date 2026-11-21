package com.hackaton.rh.attendance.controller;

import com.hackaton.rh.attendance.dto.AttendanceRequest;
import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.service.checkin.AttendanceCheckIn;
import com.hackaton.rh.attendance.service.checkout.AttendanceCheckOut;
import com.hackaton.rh.attendance.service.reader.AttendanceReader;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceReader attendanceReader;
    private final AttendanceCheckIn attendanceCheckIn;
    private final AttendanceCheckOut attendanceCheckOut;

    @GetMapping
    public List<AttendanceResponse> search(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceReader.search(employeeId, date);
    }

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceCheckIn.checkIn(request.employeeId()));
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@Valid @RequestBody AttendanceRequest request) {
        return attendanceCheckOut.checkOut(request.employeeId());
    }
}
