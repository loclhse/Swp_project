package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.ChildrenDTO;
import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Service.ChildrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ChildrenController {
    @Autowired
    private ChildrenService childrenService;

    @PostMapping("/child-create/{userId}")
    public ResponseEntity<Children> createChild(
            @PathVariable UUID userId,
            @RequestBody ChildrenDTO childrenDTO) {
        Children createdChild = childrenService.childrenCreate(userId, childrenDTO);
        return new ResponseEntity<>(createdChild, HttpStatus.CREATED); // 201
    }

    @GetMapping("/child-all")
    public ResponseEntity<List<Children>> getAllChildren() {
        List<Children> children = childrenService.getAllChildren();
        return ResponseEntity.ok(children);
    }

    @GetMapping("/child-get/{userId}/user")
    public ResponseEntity<List<Children>> getAllChildrenByUserId(@PathVariable UUID userId) {
        List<Children> children = childrenService.getAllChildrenByUserId(userId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/child-get/{childrenId}")
    public ResponseEntity<Children> getChildById(@PathVariable UUID childrenId) {
        Optional<Children> child = childrenService.getChildById(childrenId);
        return child.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/child-update/{childrenId}")
    public ResponseEntity<Children> updateChild(
            @PathVariable UUID childrenId,
            @RequestBody ChildrenDTO childrenDTO) {
        Optional<Children> updatedChild = childrenService.updateChild(childrenId, childrenDTO);
        return updatedChild.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/child-delete/{childrenId}")
    public ResponseEntity<Void> deleteChild(@PathVariable UUID childrenId) {
        childrenService.deleteChild(childrenId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/child-getByName/{name}")
        public Optional<Children>findChildrenByName(@PathVariable String name){
        return childrenService.getChildrenByName(name);
    }



}
