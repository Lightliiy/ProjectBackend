package com.example.Student.Service;

import com.example.Student.Model.Counselor;
import com.example.Student.Model.Student;
import com.example.Student.Repository.CounselorRepo;
import com.example.Student.Repository.StudentRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

	private final StudentRepo studentRepo;
	private final CounselorRepo counselorRepo;
	private final PasswordEncoder passwordEncoder;

	public StudentService(StudentRepo studentRepo, CounselorRepo counselorRepo, PasswordEncoder passwordEncoder) {
		this.studentRepo = studentRepo;
		this.counselorRepo = counselorRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public Student registerStudent(Student student) {
		student.setPassword(passwordEncoder.encode(student.getPassword()));
		return studentRepo.save(student);
	}

	public long getStudentCount() {
		return studentRepo.count();
	}

	public List<Student> getAllStudents() {
		return studentRepo.findAll();
	}

	public List<String> getAllDepartments() {
		return studentRepo.findDistinctDepartments();
	}

	public long countByDepartment(String department) {
		return studentRepo.countByDepartment(department);
	}

	public Student authenticate(String email, String rawPassword) {
		Optional<Student> optionalStudent = studentRepo.findByEmail(email);
		if (optionalStudent.isPresent()) {
			Student student = optionalStudent.get();
			if (passwordEncoder.matches(rawPassword, student.getPassword())) {
				return student;
			}
		}
		return null;
	}

	public Student saveStudent(Student student) {
		student.setPassword(passwordEncoder.encode(student.getPassword()));
		return studentRepo.save(student);
	}

	@Transactional
	public String assignStudentsToCounselors(String department) {
		List<Student> unassignedStudents = studentRepo.findByDepartmentAndCounselorIsNull(department);
		List<Counselor> counselors = counselorRepo.findByDepartment(department);

		if (counselors.isEmpty()) {
			return "No counselors found for department: " + department;
		}
		if (unassignedStudents.isEmpty()) {
			return "No unassigned students found for department: " + department;
		}

		int counselorCount = counselors.size();
		int index = 0;

		for (Student student : unassignedStudents) {
			Counselor assignedCounselor = counselors.get(index % counselorCount);

			if (assignedCounselor.getMaxCaseload() == 0 ||
					assignedCounselor.getStudents().size() < assignedCounselor.getMaxCaseload()) {
				student.setCounselor(assignedCounselor);
				studentRepo.save(student);
			}
			index++;
		}

		return "Students successfully assigned equally to counselors in department: " + department;
	}

	public Optional<Student> findByStudentId(String studentId) {
		return studentRepo.findByStudentId(studentId);
	}

	public List<Student> getStudentsByCounselorId(Long id) {
		return studentRepo.findByCounselorId(id);
	}

	public long countAllStudents() {
		return studentRepo.count();
	}

	public List<Student> getStudentsByCounselorEmail(String email) {
		return studentRepo.findByCounselorEmail(email);
	}

	public String addStudent(Student student) {
		System.out.println("Student received: " + student);
		studentRepo.save(student);
		return "Student added successfully!";
	}

	public Student updateStudent(Long id, Student updatedStudent) {
		Optional<Student> optionalStudent = studentRepo.findById(id);
		if (optionalStudent.isPresent()) {
			Student existingStudent = optionalStudent.get();

			existingStudent.setStudentId(updatedStudent.getStudentId());
			existingStudent.setName(updatedStudent.getName());
			existingStudent.setEmail(updatedStudent.getEmail());
			existingStudent.setPhone(updatedStudent.getPhone());
			existingStudent.setDepartment(updatedStudent.getDepartment());
			existingStudent.setYearLevel(updatedStudent.getYearLevel());
			existingStudent.setCounselor(updatedStudent.getCounselor());
			existingStudent.setProfileImage(updatedStudent.getProfileImage());

			return studentRepo.save(existingStudent);
		} else {
			throw new RuntimeException("Student with id " + id + " not found");
		}
	}

	public void deleteStudent(Long id) {
		if (studentRepo.existsById(id)) {
			studentRepo.deleteById(id);
		} else {
			throw new RuntimeException("Student with id " + id + " not found");
		}
	}
}
