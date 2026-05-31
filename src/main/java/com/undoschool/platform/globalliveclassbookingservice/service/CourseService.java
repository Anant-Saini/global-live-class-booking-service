package com.undoschool.platform.globalliveclassbookingservice.service;

import com.undoschool.platform.globalliveclassbookingservice.dto.requestDTOs.CourseRequestDTO;
import com.undoschool.platform.globalliveclassbookingservice.entity.Course;
import com.undoschool.platform.globalliveclassbookingservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.undoschool.platform.globalliveclassbookingservice.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Course createCourse(CourseRequestDTO dto) {
        Course course = Course.builder()
                .title(dto.title())
                .description(dto.description())
                .build();
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setTitle(dto.title());
        course.setDescription(dto.description());
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() { return courseRepository.findAll(); }
}
