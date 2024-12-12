package com.eco.volunteer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eco.volunteer.dto.AnnouncementDTO;
import com.eco.volunteer.dto.EcoKnowledgeDTO;
import com.eco.volunteer.dto.OrganizerDTO;
import com.eco.volunteer.dto.UserDTO;
import com.eco.volunteer.dto.VolunteerDTO;
import com.eco.volunteer.model.Activity;
import com.eco.volunteer.model.ActivityStatus;
import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.model.AnnouncementStatus;
import com.eco.volunteer.model.EcoKnowledge;
import com.eco.volunteer.model.Role;
import com.eco.volunteer.model.Status;
import com.eco.volunteer.model.User;
import com.eco.volunteer.repository.ActivityRepository;
import com.eco.volunteer.repository.AnnouncementRepository;
import com.eco.volunteer.repository.EcoKnowledgeRepository;
import com.eco.volunteer.repository.UserRepository;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private EcoKnowledgeRepository ecoKnowledgeRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    public List<OrganizerDTO> findAllOrganizers() {
        return userRepository.findByRole(Role.ORGANIZER)
            .stream()
            .map(this::convertToOrganizerDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public User updateOrganizerStatus(Long id, Status status) {
        User organizer = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("组织者不存在"));
            
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("该用户不是组织者");
        }
        
        organizer.setStatus(status);
        return userRepository.save(organizer);
    }
    
    public List<Activity> findOrganizerActivities(Long organizerId) {
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new RuntimeException("组织者不存在"));
            
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new RuntimeException("该用户不是组织者");
        }
        
        return activityRepository.findByOrganizerId(organizerId);
    }
    
    public List<VolunteerDTO> findAllVolunteers() {
        return userRepository.findByRole(Role.VOLUNTEER)
            .stream()
            .map(this::convertToVolunteerDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public User updateVolunteerStatus(Long id, Status status) {
        User volunteer = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("志愿者不存在"));
            
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new RuntimeException("该用户不是志愿者");
        }
        
        volunteer.setStatus(status);
        return userRepository.save(volunteer);
    }
    
    public List<Activity> findVolunteerActivities(Long volunteerId) {
        User volunteer = userRepository.findById(volunteerId)
            .orElseThrow(() -> new RuntimeException("志愿者不存在"));
            
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new RuntimeException("该用户不是志愿者");
        }
        
        return activityRepository.findByOrganizerId(volunteerId);
    }
    
    @Transactional(readOnly = true)
    public List<Activity> findActivitiesByStatus(String status) {
        if (status == null || status.equals("ALL")) {
            return activityRepository.findAll();
        }
        try {
            ActivityStatus activityStatus = ActivityStatus.valueOf(status.toUpperCase());
            return activityRepository.findByStatus(activityStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的活动状态");
        }
    }
    
    @Transactional
    public Activity updateActivityStatus(Long id, ActivityStatus status, String reason) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        
        activity.setStatus(status);
        if (reason != null && !reason.trim().isEmpty()) {
            activity.setRejectReason(reason.trim());
        }
        
        // 如果活动被通过，检查开始时间是否已到
        if (status == ActivityStatus.APPROVED && 
            activity.getStartTime().isBefore(LocalDateTime.now())) {
            activity.setStatus(ActivityStatus.ONGOING);
        }
        
        return activityRepository.save(activity);
    }
    
    public List<EcoKnowledgeDTO> findAllKnowledge() {
        return ecoKnowledgeRepository.findAll()
            .stream()
            .map(this::convertToKnowledgeDTO)
            .collect(Collectors.toList());
    }
    
    public EcoKnowledgeDTO findKnowledgeById(Long id) {
        return convertToKnowledgeDTO(ecoKnowledgeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("环保知识不存在")));
    }
    
    @Transactional
    public EcoKnowledgeDTO createKnowledge(EcoKnowledgeDTO request) {
        EcoKnowledge knowledge = new EcoKnowledge();
        knowledge.setTitle(request.getTitle());
        knowledge.setContent(request.getContent());
        knowledge.setAuthor(getCurrentUser());
        knowledge.setCreateTime(LocalDateTime.now());
        knowledge.setUpdateTime(LocalDateTime.now());
        
        return convertToKnowledgeDTO(ecoKnowledgeRepository.save(knowledge));
    }
    
    @Transactional
    public EcoKnowledgeDTO updateKnowledge(Long id, EcoKnowledgeDTO request) {
        EcoKnowledge knowledge = ecoKnowledgeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("环保知识不存在"));
        
        knowledge.setTitle(request.getTitle());
        knowledge.setContent(request.getContent());
        knowledge.setUpdateTime(LocalDateTime.now());
        
        return convertToKnowledgeDTO(ecoKnowledgeRepository.save(knowledge));
    }
    
    @Transactional
    public void deleteKnowledge(Long id) {
        ecoKnowledgeRepository.deleteById(id);
    }
    
    public List<AnnouncementDTO> findAllAnnouncements() {
        return announcementRepository.findAll()
            .stream()
            .map(this::convertToAnnouncementDTO)
            .collect(Collectors.toList());
    }
    
    public AnnouncementDTO findAnnouncementById(Long id) {
        return convertToAnnouncementDTO(announcementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("公告不存在")));
    }
    
    @Transactional
    public AnnouncementDTO createAnnouncement(AnnouncementDTO request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setAuthor(getCurrentUser());
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        
        return convertToAnnouncementDTO(announcementRepository.save(announcement));
    }
    
    @Transactional
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO request) {
        Announcement announcement = announcementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setUpdateTime(LocalDateTime.now());
        
        return convertToAnnouncementDTO(announcementRepository.save(announcement));
    }
    
    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
    
    private OrganizerDTO convertToOrganizerDTO(User user) {
        OrganizerDTO dto = new OrganizerDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setActivityCount(activityRepository.countByOrganizerId(user.getId()));
        return dto;
    }
    
    private VolunteerDTO convertToVolunteerDTO(User user) {
        VolunteerDTO dto = new VolunteerDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setActivityCount(activityRepository.countByOrganizerId(user.getId()));
        return dto;
    }
    
    private EcoKnowledgeDTO convertToKnowledgeDTO(EcoKnowledge knowledge) {
        EcoKnowledgeDTO dto = new EcoKnowledgeDTO();
        dto.setId(knowledge.getId());
        dto.setTitle(knowledge.getTitle());
        dto.setContent(knowledge.getContent());
        dto.setAuthor(convertToUserDTO(knowledge.getAuthor()));
        dto.setCreateTime(knowledge.getCreateTime());
        dto.setUpdateTime(knowledge.getUpdateTime());
        return dto;
    }
    
    private AnnouncementDTO convertToAnnouncementDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setAuthor(convertToUserDTO(announcement.getAuthor()));
        dto.setTop(announcement.isTop());
        dto.setTopTime(announcement.getTopTime());
        dto.setStatus(announcement.getStatus());
        dto.setCreateTime(announcement.getCreateTime());
        dto.setUpdateTime(announcement.getUpdateTime());
        return dto;
    }
    
    private User getCurrentUser() {
        // 这里应该从 SecurityContext 中获取当前登录用户
        // 临时返回管理员用户
        return userRepository.findByUsername("admin")
            .orElseThrow(() -> new RuntimeException("管理员用户不存在"));
    }
    
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }
} 