<%@page import="com.stpl.dimonex.model.Admin"%>
<%@page import="com.stpl.dimonex.model.Manager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="com.stpl.dimonex.model.Task"%>
<%@page import="java.util.List"%>
<%@page import="com.stpl.dimonex.model.Attendance"%>
<%@page import="com.stpl.dimonex.model.Department"%>
<%@page import="com.stpl.dimonex.model.Polisher"%>
<%@page import="com.stpl.dimonex.model.Equipment"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<style>
body {
	font-family: 'Arial', sans-serif;
	background-color: #f8f9fa;
}

.container-fluid {
	display: flex;
	height: 100vh;
}

.sidebar {
	width: 250px;
	background: #2c3e50;
	color: white;
	padding: 20px;
	min-height: 100vh;
}

.sidebar h2 {
	text-align: center;
	margin-bottom: 20px;
	font-size: 22px;
}

.sidebar a {
	display: block;
	color: white;
	text-decoration: none;
	padding: 12px;
	margin: 8px 0;
	background: #34495e;
	border-radius: 5px;
	text-align: center;
	transition: 0.3s;
}

.sidebar a:hover {
	background: #1abc9c;
}

.content {
	flex-grow: 1;
	padding: 30px;
	background: white;
	overflow-y: auto;
}

h2 {
	color: #2c3e50;
}

table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 15px;
	background: white;
	box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
	border-radius: 8px;
	overflow: hidden;
}

th, td {
	padding: 12px;
	text-align: left;
	border-bottom: 1px solid #ddd;
}

th {
	background: #1abc9c;
	color: white;
	font-weight: bold;
}

tr:nth-child(even) {
	background: #f2f2f2;
}

tr:hover {
	background: #e9f5f2;
}

.btn-custom {
	padding: 8px 12px;
	border-radius: 5px;
	border: none;
	cursor: pointer;
	font-size: 14px;
}

.btn-add {
	background: #28a745;
	color: white;
}

.btn-add:hover {
	background: #218838;
}

form {
	display: inline;
}

input, select {
	padding: 8px;
	margin: 5px;
	border: 1px solid #ddd;
	border-radius: 5px;
	width: 100%;
}
</style>
</head>
<body>
	<div class="container-fluid">
		<!-- Sidebar -->
		<div class="sidebar">
			<h2>Admin Panel</h2>
			<a href="#managers">👤 Managers</a> <a href="#polishers">🔹
				Polishers</a> <a href="#addManager">➕ Add Manager</a> <a
				href="${pageContext.request.contextPath}/order/listOrder?userId=${userId}">📜
				List Orders</a> <a
				href="${pageContext.request.contextPath}/order/placeOrder?userId=${userId}">🛒
				Place Order</a> <a href="${pageContext.request.contextPath}/admin/add">💰
				Salary</a> <a href="${pageContext.request.contextPath}/admin/expenses">💰
				Expense</a> <a
				href="${pageContext.request.contextPath}/admin/monthly-profit-expense-graph">💰
				Profit</a> <a href="CreateEquipment.jsp">Create Equipment</a>
		</div>

		<!-- Main Content -->
		<div class="content">
			<h2>Welcome, Admin</h2>

			<!-- Manager List -->
			<h3 id="managers">👤 Manager List</h3>
			<table>
				<tr>
					<th>ID</th>
					<th>Username</th>
					<th>Email</th>
					<th>Expenses</th>
					
				</tr>
				<c:forEach var="manager" items="${managers}">
					<tr>
						<td>${manager.user.id}</td>
						<td>${manager.user.username}</td>
						<td>${manager.user.email}</td>
						<td>${manager.expenses}</td>
					</tr>
				</c:forEach>
			</table>
			<div class="mt-3">
				<form
					action="${pageContext.request.contextPath}/admin/downloadManagersPdf"
					method="get">
					<button type="submit" class="btn-custom btn-add">📄
						Download Manager List (PDF)</button>
				</form>
			</div>

			<!-- Add Manager -->
			<h3 id="${pageContext.request.contextPath}/addManager">➕ Add New
				Manager</h3>
			<form action="addManager" method="post">
				<input type="text" name="username" placeholder="Username" required>
				<input type="email" name="email" placeholder="Email" required>
				<input type="password" name="password" placeholder="Password"
					required> <input type="number" step="0.01" name="expenses"
					placeholder="Expenses" required> <input type="hidden"
					name="userId" value=${userId } />
				<button type="submit" class="btn-custom btn-add">Add
					Manager</button>
			</form>

			<!-- Polisher List -->
			<h3 id="polishers">🔹 Polisher List</h3>
			<table>
				<tr>
					<th>ID</th>
					<th>Username</th>
					<th>Email</th>
					<th>Skill Level</th>
					
				</tr>
				<c:forEach var="polisher" items="${polishers}">
					<tr>
						<td>${polisher.user.id}</td>
						<td>${polisher.user.username}</td>
						<td>${polisher.user.email}</td>
						<td>${polisher.skillLevel}</td>
						
					</tr>
				</c:forEach>
			</table>

			<!-- Button to Download Polisher List as PDF -->
			<div class="mt-3">
				<form
					action="${pageContext.request.contextPath}/admin/downloadPolishersPdf"
					method="get">
					<button type="submit" class="btn-custom btn-add">📄
						Download Polisher List (PDF)</button>
				</form>
			</div>
			<!-- Main Content -->
			<div class="content">
				<h2>Welcome, Admin</h2>

				<!-- Create Equipment -->
				<h3 id="createEquipment">🔧 Create Equipment</h3>
				<form id="addEquipmentForm">
					<input type="text" id="equipmentName" placeholder="Equipment Name"
						required> <input type="text" id="equipmentType"
						placeholder="Equipment Type" required> <select
						id="equipmentStatus">
						<option value="true">Available</option>
						<option value="false">Not Available</option>
					</select>
					<button type="button" class="btn-custom btn-add"
						onclick="addEquipment()">Add Equipment</button>
				</form>

				<div class="container mt-4">
					<h2>🔧 Equipment List</h2>

					<table class="table table-bordered">
						<thead class="table-dark">
							<tr>
								<th>ID</th>
								<th>Name</th>
								<th>Type</th>
								<th>Availability</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty equipment}">
									<c:forEach var="eqs" items="${equipment}">
										<tr>
											<td>${eqs.id}</td>
											<td>${eqs.name}</td>
											<td>${eqs.type}</td>
											<td>${eqs.availabilityStatus eq 'AVAILABLE' ? '✅ Yes' : '❌ No'}</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="4" class="text-center text-danger">No
											Equipment Available</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>


					<div class="mt-3">
						<form
							action="${pageContext.request.contextPath}/admin/downloadEquipmentPdf"
							method="get">
							<button type="submit" class="btn-custom btn-add">📄
								Download Equipment List (PDF)</button>
						</form>
					</div>
				</div>

			</div>


			<div id="attendanceForPolishers" class="dashboard-section">
				<h2>Attendance For Polishers</h2>


				<form action="/dimonex/attendance/attsubmitadmin" method="POST">
					<div>
						<label for="attendanceDate">Attendance Date:</label> <input
							type="date" id="attendanceDate" name="attendanceDate"
							value="<%=request.getAttribute("attendanceDate")%>"
							onchange="fetchAttendanceData()" />
					</div>
					<table>
						<tr>
							<th>User Name</th>
							<th>Action</th>
						</tr>
						<%
						Admin admin = (Admin) request.getAttribute("admin");
						%>
						<%
						List<Manager> users = (List<Manager>) request.getAttribute("managers");
						if (users != null) {
							for (Manager user : users) {
						%>
						<tr>
							<td><%=user.getUser().getUsername()%></td>
							<td>
								<%-- <button type="submit"
							name="attendanceStatus_<%=user.getUser().getId()%>"
							value="Present" class="present">Present</button>
						<button type="submit"
							name="attendanceStatus_<%=user.getUser().getId()%>"
							value="Absent" class="absent">Absent</button> <input
						type="hidden" name="userId_<%=user.getUser().getId()%>"
						value="<%=user.getId()%>" /> --%> <input id="presentradio" type="radio"
								name="attendanceStatus_<%=user.getUser().getId()%>"
								value="Present"> Present</input> <input type="radio"
								name="attendanceStatus_<%=user.getUser().getId()%>"
								value="Absent"> Absent</input> <input type="hidden"
								name="userId_<%=user.getId()%>" value="<%=user.getId()%>" /> <%-- <% Manager managerss = (Manager) request.getAttribute("manager");
 %>--%> <input type="hidden" name="admin" value="<%=admin.getId()%>" />
							</td>
						</tr>
						<%
						}
						}
						%>
					</table>
					<button type="submit">Submit Attendance</button>
				</form>
			</div>

			<div class="mt-3">
				<form
					action="${pageContext.request.contextPath}/admin/downloadAttendancePdf"
					method="get">
					<button type="submit" class="btn-custom btn-add">📄
						Download Attendance (PDF)</button>
				</form>
			</div>




			<script>
			// Function to fetch attendance data
		function fetchAttendanceData() {
			    var selectedDate = document.getElementById('attendanceDate').value;

			    // Send a GET request to fetch attendance data for the selected date
			    fetch('<%=request.getContextPath()%>/attendance/attendance-data?attendanceDate=' + selectedDate, {
			        method: 'GET'
			    })
			    .then(response => response.json()) // Assuming the response is in JSON format
			    .then(data => {
			        document.querySelectorAll("input[name^='attendanceStatus_']").forEach(input => {
			            input.checked = false; // Uncheck all inputs first
			        });

			        // Iterate over the returned attendance data and pre-select the radio buttons
			        console.log(data);
			        if (data.length !== 0) {
			            data.forEach(function (attendance) {
			                var userId = attendance.userId;
			                var status = attendance.status;
			                var presentRadio = document.querySelector("input[name='attendanceStatus_" + userId + "'][value='Present']");
			                var absentRadio = document.querySelector("input[name='attendanceStatus_" + userId + "'][value='Absent']");
                             if(presentRadio && absentRadio){
			                // Check the radio button based on the status
			                if (status === "Present") {
			                    presentRadio.checked = true;
			                } else if (status === "Absent") {
			                    absentRadio.checked = true;
			                } else {
			                    presentRadio.checked = false;
			                    absentRadio.checked = false;
			                }
                             }
			            });
			        } else {
			            document.querySelectorAll("input[name^='attendanceStatus_']").forEach(input => {
			                input.checked = false; // Uncheck all radio buttons if no data is found
			            });
			        }
			    })
			    .catch(error => {
			        console.error('Error fetching attendance data:', error);
			    });
			}

			// Trigger initial fetch on page load (for today by default)
			document.addEventListener('DOMContentLoaded', function () {
			    fetchAttendanceData();
			});

			// Function to add equipment
			function addEquipment() {
			    let name = document.getElementById("equipmentName").value;
			    let type = document.getElementById("equipmentType").value;
			    let availableStatus = document.getElementById("equipmentStatus").value;
			    
			    console.log(name);
			    console.log(type);
			    
			    if (!name || !type) {
			        alert("Please fill all fields!");
			        return;
			    }

			    fetch('${pageContext.request.contextPath}/api/equipment/add', {
			        method: 'POST',
			        headers: {
			            'Content-Type': 'application/json'
			        },
			        body: JSON.stringify({
			            name: name,
			            type: type,
			            availableStatus: availableStatus === "true"
			        })
			    })
			    .then(response => response.text())
			    .then(response => {
			        alert(response);
			        location.reload(); // Refresh page to update equipment list
			    })
			    .catch(error => {
			        alert("Error adding equipment: " + error.message);
			    });
			} 
			
			
		<%-- 	function fetchAttendanceData() {
			    var selectedDate = document.getElementById('attendanceDate').value;

			    // Send a GET request to fetch attendance data for the selected date
			    fetch('<%=request.getContextPath()%>/attendance/attendance-data?attendanceDate=' + selectedDate, {
			        method: 'GET'
			    })
			    .then(response => response.json()) // Assuming the response is in JSON format
			    .then(data => {
			        // Log the data to inspect the structure
			        console.log(data);

			        // Uncheck all inputs first (clear any previously selected radio buttons)
			        document.querySelectorAll("input[name^='attendanceStatus_']").forEach(input => {
			            input.checked = false;
			        });

			        // Check if data is returned and iterate over the response
			        if (Array.isArray(data) && data.length > 0) {
			            data.forEach(function (attendance) {
			                var userId = attendance.userId;
			                var status = attendance.status;

			                // Try to find the corresponding radio buttons for each user
			                var presentRadio = document.querySelector(`input[name='attendanceStatus_${userId}'][value='Present']`);
			                var absentRadio = document.querySelector(`input[name='attendanceStatus_${userId}'][value='Absent']`);
			             
			                console.log("present Radio is ",presentRadio)
			                // Check if both radio buttons exist before setting 'checked'
			                if (presentRadio && absentRadio) {
			                    if (status === "Present") {
			                        presentRadio.checked = true;
			                    } else if (status === "Absent") {
			                        absentRadio.checked = true;
			                    }
			                } else {
			                    // Debugging: Log if elements are not found
			                    console.warn(`Radio buttons not found for user ${userId}`);
			                }
			            });
			        } else {
			            // If no data found, uncheck all radio buttons
			            document.querySelectorAll("input[name^='attendanceStatus_']").forEach(input => {
			                input.checked = false; // Uncheck all radio buttons
			            });
			        }
			    })
			    .catch(error => {
			        console.error('Error fetching attendance data:', error);
			    });
			}

			// Trigger initial fetch on page load (for today by default)
			document.addEventListener('DOMContentLoaded', function () {
			    fetchAttendanceData();
			}); --%>


			</script>




			<script
				src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
