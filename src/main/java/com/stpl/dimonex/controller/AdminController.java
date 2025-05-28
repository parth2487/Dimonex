
package com.stpl.dimonex.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stpl.dimonex.model.Admin;
import com.stpl.dimonex.model.Attendance;
import com.stpl.dimonex.model.Department;
import com.stpl.dimonex.model.Equipment;
import com.stpl.dimonex.model.Expense;
import com.stpl.dimonex.model.Manager;
import com.stpl.dimonex.model.ManagerSalary;
import com.stpl.dimonex.model.MonthlyFinancialSummary;
import com.stpl.dimonex.model.Polisher;
import com.stpl.dimonex.model.Task;
import com.stpl.dimonex.service.AdminService;
import com.stpl.dimonex.service.AttendanceService;
import com.stpl.dimonex.service.DepartmentService;
import com.stpl.dimonex.service.EquipmentService;
import com.stpl.dimonex.service.ExpenseService;
import com.stpl.dimonex.service.ManagerSalaryService;
import com.stpl.dimonex.service.ManagerService;
import com.stpl.dimonex.service.PolisherService;
import com.stpl.dimonex.service.TaskService;
import com.stpl.dimonex.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ManagerService mangerSerive;

	@Autowired
	private PolisherService polisherService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private ManagerService managerService;

	@Autowired
	private UserService userService;

	@Autowired
	private ExpenseService expenseService;

	@Autowired
	private ManagerSalaryService managerSalaryService;

	@GetMapping("/dashboard")
	public String showDashboard(@RequestParam("userId") Long userId,
			@RequestParam(value = "attendanceDate", required = false) String attendanceDate, Model model) {

		// Fetch Admin details using userId
		System.out.println("--------------USER ID IS" + userId);
		Admin admin = adminService.getAdminByUserId(userId);

		List<Manager> managers = mangerSerive.getAllManagers();

		List<Polisher> polishers = polisherService.getAllPolishers();

		List<Department> departments = departmentService.getAllDepartments();

		List<Equipment> equipment = equipmentService.getAllEquipment();

		if (attendanceDate == null || attendanceDate.isEmpty()) {
			attendanceDate = LocalDate.now().toString();
		}

		model.addAttribute("currentDate", LocalDate.now().toString());
		model.addAttribute("attendanceDate", attendanceDate);

		// Pass admin details to the model
		model.addAttribute("admin", admin);

		model.addAttribute("userId", userId);
		model.addAttribute("managers", managers);

		model.addAttribute("polishers", polishers);

		model.addAttribute("equipment", equipment);

		model.addAttribute("departments", departments);

		return "adminDashboard";
	}

	@PostMapping("/assignTask")
	public String assignTask(@RequestParam Long managerId, @RequestParam String taskName,
			@RequestParam String description, @RequestParam int numberOfDiamonds, @RequestParam String deadline,
			@RequestParam String track, @RequestParam("userId") Long userId) { // Add userId parameter

		System.out.print("USER ID IS" + userId);
		taskService.assignTask(managerId, taskName, description, numberOfDiamonds, deadline, track);
		return "redirect:/admin/dashboard?userId=" + userId; // Append userId to redirect
	}

	@PostMapping("/addManager")
	public String addManager(@RequestParam String username, @RequestParam String email, @RequestParam String password,
			@RequestParam double expenses, @RequestParam Long userId, Model model) {

		managerService.createManager(username, password, email, expenses, 1);

		model.addAttribute("message", "Manager added successfully!");
		return "redirect:/admin/dashboard?userId=" + userId;
	}

	@PostMapping("/deleteManager")
	public String deleteManager(@RequestParam("id") Long managerId, @RequestParam("userId") Long userId) {
		System.out.println("----------Manager Id" + managerId);

		List<Manager> managers = managerService.getAllManagers();
		Long idis = managerId;
		for (Manager manager : managers) {
			if (manager.getUser().getId() == managerId) {
				idis = manager.getId();
			}
		}

		managerService.deleteManager(idis);
		return "redirect:/admin/dashboard?userId=" + userId; // Redirect to the admin dashboard after deletion
	}

	@PostMapping("/deletePolisher")
	public String deletePolisher(@RequestParam("id") Long polisherId, @RequestParam("userId") Long userId) {
		System.out.println("---------- Polisher Id: " + polisherId);

		// Fetch all polishers
		List<Polisher> polishers = polisherService.getAllPolishers();
		Long idToDelete = polisherId;

		// Find the actual Polisher ID if stored inside another entity
		for (Polisher polisher : polishers) {
			if (polisher.getUser().getId().equals(polisherId)) {
				idToDelete = polisher.getId();
				break;
			}
		}

		// Delete the polisher
		polisherService.deletePolisher(idToDelete);

		// Redirect to the admin dashboard
		return "redirect:/admin/dashboard?userId=" + userId;
	}

	@GetMapping("/expenses")
	public String showCompanyExpenses(Model model) {
		List<Expense> expenses = expenseService.getAllExpenses();
		expenseService.calculateAndSaveMonthlyExpenses();
		System.out.print("----PRINTINING EXP---" + expenses.get(0).getSalaryExpense());
		model.addAttribute("expenses", expenses);
		return "admin-expense-summary";
	}

	// 🔹 Show salary form to add new salary
	@GetMapping("/add")
	public String showAddSalaryForm(Model model) {
		List<Manager> managers = managerService.getAllManagers();
		model.addAttribute("managers", managers);
		model.addAttribute("currentYear", LocalDate.now().getYear()); // add this line
		return "manager-salary-form"; // Create this JSP for form input
	}

	@PostMapping("/add")
	public String addSalary(@RequestParam("managerId") Long managerId, @RequestParam("month") int month,
			@RequestParam("year") int year, @RequestParam("amount") double amount, Model model) {
		Manager manager = managerService.getManagerByUserId(managerId);
		if (manager == null) {
			model.addAttribute("error", "Manager not found.");
			return "error";
		}

		// Prevent duplicate entry
		boolean alreadyPaid = managerSalaryService.isSalaryAlreadyPaid(managerId, month, year);
		if (alreadyPaid) {
			model.addAttribute("message", "Salary already processed for this month.");
			return "manager-salary-form";
		}

		managerSalaryService.processManagerSalary(manager, month, year, amount);
		return "manager-salary-form";
	}

	@GetMapping("/manager-salary-logs")
	public String viewAllManagerSalaries(Model model) {
		List<ManagerSalary> salaryLogs = managerSalaryService.getAllSalaries();
		model.addAttribute("salaryLogs", salaryLogs);
		return "managerSalaryLogs"; // Name of the JSP page we'll create next
	}

	@GetMapping("/monthly-expense-graph")
	public String showMonthlyGraphPage(Model model) {
		List<Expense> expenses = expenseService.getAllExpenses();
		model.addAttribute("expenses", expenses);
		return "monthly-expense-graph"; // JSP file
	}

	@GetMapping("/monthly-profit-expense-graph")
	public String showProfitExpenseGraph(Model model) {
		List<MonthlyFinancialSummary> summaries = expenseService.getMonthlyProfitAndExpenseSummary();
		System.out.println("All the summeries ne bhai "+summaries);
		model.addAttribute("summaries", summaries);
		return "profit-expense-graph"; // JSP file name
	}

//    @GetMapping("/downloadManagersPdf")
//    public void downloadManagersPdf(HttpServletResponse response) throws DocumentException, IOException {
//        // Create a document
//        Document document = new Document();
//
//        // Set content type as PDF
//        response.setContentType("application/pdf");
//
//        // Set content disposition as an inline PDF
//        response.setHeader("Content-Disposition", "attachment; filename=\"managers_list.pdf\"");
//
//        // Create PdfWriter instance
//        PdfWriter.getInstance(document, response.getOutputStream());
//
//        // Open the document to add content
//        document.open();
//
//        // Add some content (Header)
//        document.add(new Paragraph("Manager List"));
//        document.add(new Paragraph("================================="));
//
//        // Fetch manager data
//        List<Manager> managers = managerService.getAllManagers();  // Fetch managers from DB
//        List<Polisher> polishers = polisherService.getAllPolishers();
//
//		
//        // Add manager data to PDF
//        for (Manager manager : managers) {
//        	List<Polisher> pop = new ArrayList<>();
//        	for (Polisher polish : polishers) {
//    			if (polish.getManager().getId() == manager.getId()) {
//    				pop.add(polish);
//    			}
//    		}
//            document.add(new Paragraph("ID: " + manager.getId()));
//            document.add(new Paragraph("Username: " + manager.getUser().getUsername()));
//            document.add(new Paragraph("Email: " + manager.getUser().getEmail()));
//            document.add(new Paragraph("Expenses: " + manager.getExpenses()));
//            
//            document.add(new Paragraph("================================="));
//        }
//
//        // Close the document
//        document.close();
//    }

//	@GetMapping("/downloadManagersPdf")
//	public void downloadManagersPdf(HttpServletResponse response) throws DocumentException, IOException {
//	    // Create a document
//	    Document document = new Document();
//
//	    // Set content type as PDF
//	    response.setContentType("application/pdf");
//
//	    // Set content disposition as an inline PDF
//	    response.setHeader("Content-Disposition", "attachment; filename=\"managers_list.pdf\"");
//
//	    // Create PdfWriter instance
//	    PdfWriter.getInstance(document, response.getOutputStream());
//
//	    // Open the document to add content
//	    document.open();
//
//	    // Add some content (Header)
//	    document.add(new Paragraph("Manager List"));
//	    document.add(new Paragraph("================================="));
//
//	    // Fetch manager data
//	    List<Manager> managers = managerService.getAllManagers();  // Fetch managers from DB
//	    List<Polisher> polishers = polisherService.getAllPolishers();  // Fetch all polishers from DB
//
//	    // Add manager data to PDF
//	    for (Manager manager : managers) {
//	        document.add(new Paragraph("ID: " + manager.getId()));
//	        document.add(new Paragraph("Username: " + manager.getUser().getUsername()));
//	        document.add(new Paragraph("Email: " + manager.getUser().getEmail()));
//	        document.add(new Paragraph("Expenses: " + manager.getExpenses()));
//	        
//	        document.add(new Paragraph("================================="));
//
//	        // Find the polishers associated with this manager
//	        List<Polisher> managerPolishers = new ArrayList<>();
//	        for (Polisher polisher : polishers) {
//	            if (polisher.getManager() != null && polisher.getManager().getId().equals(manager.getId())) {
//	                managerPolishers.add(polisher);
//	            }
//	        }
//
//	        if (!managerPolishers.isEmpty()) {
//	            document.add(new Paragraph("Polishers under this Manager:"));
//	            document.add(new Paragraph("------------------------------"));
//
//	            // Add details of each polisher under this manager
//	            for (Polisher polisher : managerPolishers) {
//	                document.add(new Paragraph("Polisher ID: " + polisher.getId()));
//	                document.add(new Paragraph("Skill Level: " + polisher.getSkillLevel()));
//	                document.add(new Paragraph("Experience Level: " + polisher.getExperienceLevel()));
////	                document.add(new Paragraph("Available Status: " + (polisher.getAvailableStatus() ? "Available" : "Not Available")));
//
//	                // Optionally, add task details if needed (uncomment to include)
//	                
//	               List<Task>tasks= taskService.getAllTasks();
//	                
//	                
//	                    document.add(new Paragraph("Assigned Tasks:"));
//	                    for (Task task : tasks) {
//	                    	if(task.getPolisher().getId()==polisher.getId())
//	                        document.add(new Paragraph("Task Name: " + task.getTaskName()));
//	                        document.add(new Paragraph("Deadline: " + task.getDeadline()));
//	                        document.add(new Paragraph("Status: " + task.getTrack()));
//	                    }
//	                
//	                
//
//	                document.add(new Paragraph("------------------------------"));
//	            }
//	        } else {
//	            document.add(new Paragraph("No polishers under this manager."));
//	        }
//
//	        document.add(new Paragraph("================================="));
//	    }
//
//	    // Close the document
//	    document.close();
//	}
//
//	
//	

	@GetMapping("/downloadManagersPdf")
	public void downloadManagersPdf(HttpServletResponse response) throws DocumentException, IOException {
		// Create a document
		Document document = new Document(PageSize.A4);

		// Set content type as PDF
		response.setContentType("application/pdf");

		// Set content disposition as an inline PDF
		response.setHeader("Content-Disposition", "attachment; filename=\"managers_list.pdf\"");

		// Create PdfWriter instance
		PdfWriter.getInstance(document, response.getOutputStream());

		// Open the document to add content
		document.open();

		// Add some content (Header)
		Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
		Paragraph header = new Paragraph("Manager List", headerFont);
		header.setAlignment(Element.ALIGN_CENTER);
		document.add(header);
		document.add(new Paragraph("\n"));

		// Add some divider
		document.add(new Paragraph("================================="));

		// Fetch manager data
		List<Manager> managers = managerService.getAllManagers(); // Fetch managers from DB
		List<Polisher> polishers = polisherService.getAllPolishers(); // Fetch all polishers from DB

		// Iterate through each manager and display their data
		for (Manager manager : managers) {
			// Add manager information
			Font managerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Paragraph managerTitle = new Paragraph("Manager Information", managerFont);
			managerTitle.setSpacingBefore(10);
			document.add(managerTitle);

			// Manager details
			Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
			document.add(new Paragraph("ID: " + manager.getId(), normalFont));
			document.add(new Paragraph("Username: " + manager.getUser().getUsername(), normalFont));
			document.add(new Paragraph("Email: " + manager.getUser().getEmail(), normalFont));
			document.add(new Paragraph("Expenses: " + manager.getExpenses(), normalFont));
			document.add(new Paragraph("\n"));

			// Add a divider between manager sections
			document.add(new Paragraph("================================="));

			// Find the polishers associated with this manager
			List<Polisher> managerPolishers = new ArrayList<>();
			for (Polisher polisher : polishers) {
				if (polisher.getManager() != null && polisher.getManager().getId().equals(manager.getId())) {
					managerPolishers.add(polisher);
				}
			}

			// Display polishers under this manager
			if (!managerPolishers.isEmpty()) {
				Font polisherTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
				Paragraph polisherTitle = new Paragraph("Polishers under this Manager", polisherTitleFont);
				polisherTitle.setSpacingBefore(10);
				document.add(polisherTitle);

				// Add a table for polishers
				PdfPTable polisherTable = new PdfPTable(4);
				polisherTable.setWidthPercentage(100); // Set table width to fill the page
				polisherTable.setSpacingBefore(10);

				// Add table headers
				polisherTable.addCell(new Phrase("Polisher ID"));
				polisherTable.addCell(new Phrase("Skill Level"));
				polisherTable.addCell(new Phrase("Experience Level"));
				polisherTable.addCell(new Phrase("Availability"));

				// Add details of each polisher
				for (Polisher polisher : managerPolishers) {
					polisherTable.addCell(String.valueOf(polisher.getId()));
					polisherTable.addCell(polisher.getSkillLevel());
					polisherTable.addCell(polisher.getExperienceLevel());
//	                polisherTable.addCell(polisher.getAvailableStatus() ? "Available" : "Not Available");

					// Optionally, add task details (if tasks are assigned to this polisher)
					List<Task> tasks = taskService.getAllTasks();
					List<Task> polisherTasks = new ArrayList<>();
					for (Task task : tasks) {
						if (task.getPolisher().getId().equals(polisher.getId())) {
							polisherTasks.add(task);
						}
					}

					// Add task info under the polisher row if tasks are present
					if (!polisherTasks.isEmpty()) {
						for (Task task : polisherTasks) {
							document.add(new Paragraph("  Task: " + task.getTaskName() + " | Deadline: "
									+ task.getDeadline() + " | Status: " + task.getTrack(), normalFont));
						}
					}
				}

				// Add the table to the document
				document.add(polisherTable);
			} else {
				// If no polishers, mention it
				document.add(new Paragraph("No polishers under this manager.", normalFont));
			}

			document.add(new Paragraph("\n"));
			document.add(new Paragraph("================================="));
		}

		// Close the document
		document.close();
	}

	@GetMapping("/downloadPolishersPdf")
	public void downloadPolishersPdf(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"polishers_list.pdf\"");
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();

		document.add(new Paragraph("Polisher List"));
		document.add(new Paragraph("================================="));

		List<Polisher> polishers = polisherService.getAllPolishers(); // Fetch polishers from DB
		for (Polisher polisher : polishers) {
			document.add(new Paragraph("ID: " + polisher.getId()));
			document.add(new Paragraph("Username: " + polisher.getUser().getUsername()));
			document.add(new Paragraph("Skill Level: " + polisher.getSkillLevel()));
			document.add(new Paragraph("Experience Level: " + polisher.getExperienceLevel()));
//	            document.add(new Paragraph("Availability: " + (polisher.getAvailableStatus() ? "Available" : "Not Available")));
			document.add(new Paragraph("================================="));
		}

		document.close();
	}

	@GetMapping("/downloadEquipmentPdf")
	public void downloadEquipmentPdf(HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"equipment_list.pdf\"");
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();

		document.add(new Paragraph("Equipment List"));
		document.add(new Paragraph("================================="));

		List<Equipment> equipmentList = equipmentService.getAllEquipment(); // Fetch equipment from DB
		for (Equipment equipment : equipmentList) {
			document.add(new Paragraph("ID: " + equipment.getId()));
			document.add(new Paragraph("Name: " + equipment.getName()));
			document.add(new Paragraph("Type: " + equipment.getType()));
//			document.add(new Paragraph(
//					"Availability: " + (equipment.getAvailabilityStatus() ? "Available" : "Not Available")));
			document.add(new Paragraph("================================="));
		}

		document.close();
	}

	@GetMapping("/downloadAttendancePdf")
    public void downloadAttendancePdf(HttpServletResponse response) throws DocumentException, IOException {
        // Fetch the attendance records
        List<Attendance> attendanceRecords = attendanceService.getAllAttendance();

        // Set the response headers for the PDF download
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_records.pdf");

        // Create a new document
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();

        // Adding a title to the PDF
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Attendance Records", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Add a table to display the attendance records
        PdfPTable table = new PdfPTable(4); // 4 columns

        // Setting table headers
        table.addCell("ID");
        table.addCell("User ID");
        table.addCell("Status");
        table.addCell("Date");

        // Adding data rows from the attendance records
        for (Attendance attendance : attendanceRecords) {
            table.addCell(String.valueOf(attendance.getId()));
            table.addCell(String.valueOf(attendance.getUserId()));
            table.addCell(attendance.getStatus());
            table.addCell(attendance.getDate());
        }

        // Add the table to the document
        document.add(table);

        // Closing the document
        document.close();

        // Write the PDF content to the response output stream
        response.getOutputStream().write(byteArrayOutputStream.toByteArray());
        response.getOutputStream().flush();
    }

}