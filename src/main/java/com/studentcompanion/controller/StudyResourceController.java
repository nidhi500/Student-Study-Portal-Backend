package com.studentcompanion.controller;

import com.studentcompanion.model.*;
import com.studentcompanion.repository.UnitRepository;
import com.studentcompanion.service.StudyResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://student-study-portal-frontend.vercel.app"
}, allowCredentials = "true")
public class StudyResourceController {

    @Autowired
    private StudyResourceService studyService;

    @Autowired
    private UnitRepository unitRepository;

    // 🔹 GET all semesters
    @GetMapping("/semesters")
    public List<Semester> getSemesters() {
        return studyService.getAllSemesters();
    }

    // 🔹 GET all subjects for semester + branch
    @GetMapping("/semesters/{semester}/subjects")
    public List<Subject> getSubjects(@PathVariable int semester, @RequestParam String branch) {
        return studyService.getSubjectsBySemesterAndBranch(semester, branch);
    }

    // 🔹 GET units by subject ID
    @GetMapping("/subjects/{id}/units")
    public List<Unit> getUnits(@PathVariable Long id) {
        System.out.println("📦 Fetching units for subjectId: " + id);
        return studyService.getUnitsBySubject(id);
    }

    // 🔹 GET unit by ID
    @GetMapping("/units/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return unitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 GET resources for a unit
    @GetMapping("/units/{id}/resources")
    public List<Resource> getResources(@PathVariable Long id) {
        return studyService.getResourcesByUnit(id);
    }

    // 🔹 GET PYQs for a unit
    @GetMapping("/units/{id}/pyqs")
    public List<Pyq> getPyqs(@PathVariable Long id) {
        return studyService.getPyqsByUnit(id);
    }

    // 🔹 ADMIN: Create Semester
    @PostMapping("/admin/semesters")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Semester createSemester(@RequestBody Semester semester) {
        return studyService.createSemester(semester);
    }

    // 🔹 ADMIN: Create Subject
    @PostMapping("/admin/subjects")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Subject createSubject(@RequestBody Subject subject) {
        return studyService.createSubject(subject);
    }

    // 🔹 ADMIN: Create Unit
    @PostMapping("/admin/units")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Unit createUnit(@RequestBody Unit unit) {
        return studyService.createUnit(unit);
    }

    // 🔹 ADMIN: Upload PDF Resource
    @PostMapping("/admin/resources/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Resource> uploadPdfResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("unitId") Long unitId,
            @RequestParam("type") ResourceType type
    ) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        file.transferTo(new File(filePath));

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found: " + unitId));

        Resource resource = new Resource();
        resource.setTitle(title);
        resource.setType(type);
        resource.setUnit(unit);
        resource.setUrl("/uploads/" + fileName); // will be served by WebConfig

        Resource saved = studyService.createResource(resource);
        return ResponseEntity.ok(saved);
    }
}
