package com.studentcompanion.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentcompanion.dto.ContributionDTO;
import com.studentcompanion.model.Contribution;
import com.studentcompanion.model.User;
import com.studentcompanion.repository.ContributionRepository;
import com.studentcompanion.repository.UserRepository;

@RestController
@RequestMapping("/api/contributions")
@CrossOrigin(origins = "*")
public class ContributionController {
@Autowired
private ContributionRepository contributionRepository;

@Autowired
private UserRepository userRepository;

// @Autowired
// private CommentRepository commentRepository;
@PostMapping("/add")
public ResponseEntity<?> addContribution(@RequestBody ContributionDTO dto, Authentication authentication) {
    System.out.println("🎯 Inside /api/contributions/add");
    if (dto == null) {
        System.out.println("❌ DTO is null");
        return ResponseEntity.badRequest().body("❌ DTO is null");
    }

    try {
        System.out.println("✅ DTO Title: " + dto.getTitle());
        System.out.println("✅ DTO Type: " + dto.getType());
        System.out.println("✅ DTO Subject: " + dto.getSubject());
        System.out.println("✅ DTO Visibility: " + dto.getVisibility());
        System.out.println("✅ DTO URL: " + dto.getUrl());

        String email = (authentication != null) ? authentication.getName() : "agrawalnidhi241@gmail.com";
        System.out.println("📧 Authenticated Email: " + email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("❌ User not found");
            return ResponseEntity.status(404).body("User not found");
        }

        Contribution contribution = new Contribution();
        contribution.setTitle(dto.getTitle());
        contribution.setDescription(dto.getDescription());
        contribution.setType(dto.getType());
        contribution.setSubject(dto.getSubject());
        contribution.setVisibility(dto.getVisibility());
        contribution.setUrl(dto.getUrl());
        contribution.setUser(user);
        contribution.setCreatedAt(LocalDateTime.now());

        System.out.println("💾 Saving contribution...");
        contributionRepository.save(contribution);
        System.out.println("✅ Contribution saved");

        return ResponseEntity.ok("✅ Contribution saved");
    } catch (Exception e) {
        System.out.println("❌ Exception occurred:");
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("❌ Server Error: " + e.getMessage());
    }
}




@GetMapping("/my")
public ResponseEntity<?> getUserContributions(Authentication authentication) {
    try {
        String email = (authentication != null) ? authentication.getName() : "agrawalnidhi241@gmail.com";
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Contribution> list = contributionRepository.findByUser(user);
        return ResponseEntity.ok(list);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("❌ Failed to fetch contributions: " + e.getMessage());
    }
}

@GetMapping("/{id}/upvotes")
public ResponseEntity<?> getUpvotes(@PathVariable Long id) {
Contribution c = contributionRepository.findById(id).orElseThrow();
return ResponseEntity.ok(c.getUpvotes());
}

@GetMapping("/{id}/downvotes")
public ResponseEntity<?> getDownvotes(@PathVariable Long id) {
Contribution c = contributionRepository.findById(id).orElseThrow();
return ResponseEntity.ok(c.getDownvotes());
}

@GetMapping("/{id}/bookmarks")
public ResponseEntity<?> getBookmarks(@PathVariable Long id) {
Contribution c = contributionRepository.findById(id).orElseThrow();
return ResponseEntity.ok(c.getBookmarks());
}


}