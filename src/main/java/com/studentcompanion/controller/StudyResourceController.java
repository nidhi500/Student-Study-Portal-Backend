package com.studentcompanion.controller;

import com.studentcompanion.model.*;
import com.studentcompanion.repository.UnitRepository;
import com.studentcompanion.service.StudyResourceService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.studentcompanion.repository.PyqRepository;



import java.io.File;
import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")

public class StudyResourceController {

    @Autowired
    private StudyResourceService studyService;

    // 👨‍🎓 STUDENT: Fetch Semesters, Subjects, Units, Resources, PYQs, Comments
    @Autowired
    private UnitRepository unitRepository;



    @GetMapping("/semesters")
    public List<Semester> getSemesters() {
        return studyService.getAllSemesters();
    }

    @GetMapping("/semesters/{semester}/subjects")
    public List<Subject> getSubjects(@PathVariable int semester, @RequestParam String branch) {
        return studyService.getSubjectsBySemesterAndBranch(semester, branch);
    }


    @GetMapping("/subjects/{id}/units")
    public List<Unit> getUnits(@PathVariable Long id) {
        return studyService.getUnitsBySubject(id);
    }
    @GetMapping("/units/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return unitRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/units/{id}/resources")
    public List<Resource> getResources(@PathVariable Long id) {
        return studyService.getResourcesByUnit(id);
    }

    @GetMapping("/units/{id}/pyqs")
    public List<Pyq> getPyqs(@PathVariable Long id) {
        return studyService.getPyqsByUnit(id);
    }

    @PostMapping("/admin/semesters")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Semester createSemester(@RequestBody Semester semester) {
        return studyService.createSemester(semester);
    }

    @PostMapping("/admin/subjects")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Subject createSubject(@RequestBody Subject subject) {
        return studyService.createSubject(subject);
    }

    @PostMapping("/admin/units")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Unit createUnit(@RequestBody Unit unit) {
        return studyService.createUnit(unit);
    }
    @PostMapping("/admin/resources/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Resource> uploadPdfResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("unitId") Long unitId,
            @RequestParam("type") ResourceType type
    ) throws IOException {

    // ✅ Use absolute path
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
    resource.setUrl("/uploads/" + fileName); // This will be served by WebConfig

    Resource saved = studyService.createResource(resource);
    return ResponseEntity.ok(saved);
}

    
}
