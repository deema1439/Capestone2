package com.example.capestone2.Controller;

import com.example.capestone2.Api.ApiRes;
import com.example.capestone2.Model.Role;
import com.example.capestone2.Service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/get")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.status(200).body(roleService.getRoles());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody @Valid Role role, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = roleService.addRole(role);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("role added"));
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody @Valid Role role, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiRes(errors.getFieldError().getDefaultMessage()));
        }
        int result = roleService.updateRole(id, role);

        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("role not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("game not found"));
            case 2:
                return ResponseEntity.status(400).body(new ApiRes("role is in use, cannot change its game"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("role updated"));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id){
        int result = roleService.deleteRole(id);
        switch (result){
            case 0:
                return ResponseEntity.status(400).body(new ApiRes("role not found"));
            case 1:
                return ResponseEntity.status(400).body(new ApiRes("role is in use, cannot delete"));
            default:
                return ResponseEntity.status(200).body(new ApiRes("role deleted"));
        }
    }










}
