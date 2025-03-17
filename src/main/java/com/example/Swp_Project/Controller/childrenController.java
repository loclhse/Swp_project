package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Service.childrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class childrenController {
        @Autowired
        private childrenService childrenService;

        @GetMapping
        public List<Children> getAllChildren() {
            return childrenService.getAllChildren();
        }

        @GetMapping("/children-get/{id}")
        public ResponseEntity<Children> getChildrenById(@PathVariable(value = "id") UUID childrenId) {
            Children children = childrenService.getChildrenById(childrenId);
            if (children == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(children);
        }

        @PutMapping("/children-update/{id}")
        public ResponseEntity<Children> updateChildren(@PathVariable(value = "id") UUID childrenId, @RequestBody Children childrenDetails) {
            Children updatedChildren = childrenService.updateChildren(childrenId, childrenDetails);
            if (updatedChildren == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(updatedChildren);
        }

        @DeleteMapping("/children-delete/{id}")
        public ResponseEntity<Void> deleteChildren(@PathVariable(value = "id") UUID childrenId) {
            childrenService.deleteChildren(childrenId);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/children-get/{userId}")
        public ResponseEntity<List<Children>> getChildrenByUserId(@PathVariable(value = "userId") UUID userId) {
        List<Children> childrenList = childrenService.getChildrenByUserId(userId);
        if (childrenList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(childrenList);
    }
}
