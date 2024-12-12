package com.eco.volunteer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.eco.volunteer.dto.LoginRequest;
import com.eco.volunteer.dto.RegisterRequest;
import com.eco.volunteer.model.Role;
import com.eco.volunteer.model.Status;
import com.eco.volunteer.model.User;
import com.eco.volunteer.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public ResponseEntity<?> login(LoginRequest request) {
        // 检查是否是管理员账号
        if ("admin".equals(request.getUsername()) && "admin".equals(request.getPassword())) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setRole(Role.ADMIN);
            adminUser.setStatus(Status.ACTIVE);
            return ResponseEntity.ok(adminUser);
        }
        
        // 检查数据库中的用户
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));
            
        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("账号未激活或已被禁用");
        }
        
        return ResponseEntity.ok(user);
    }
    
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() != null ? request.getRole() : Role.VOLUNTEER);
        user.setStatus(Status.ACTIVE);  // 直接设置为激活状态
        
        return ResponseEntity.ok(userRepository.save(user));
    }
} 