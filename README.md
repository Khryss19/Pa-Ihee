# Pa-Ihee
**Project Description**
This project aims to address these problems through a system that records and shows the status of each CR. It is important because it helps people save time and allows maintenance staff to manage repairs in an organized manner.

**Problem being addressed**
The school does not have a way of informing people which comfort rooms (CRs) are working and which are under maintenance. As a result, students and employees may go to a CR only to find it closed for repair or cleaning. This wastes time and causes inconvenience, especially during short breaks.Maintenance staff also face a problem. Without a proper record, it is difficult to know which CRs need repair, what the problem is, and when each one was last checked.

**Project objectives**
General Objective
To develop a Java-based application that will help users check the availability of the stalls of the comfort room

Specific Objectives
The project aims to:
-Keep a list of comfort rooms and their locations
-Show whether the comfort room is working or under maintenance
-Allow staff to update the status of the comfort room and add a short reason
-Record the date and time for each update
-Keep a history of maintenance for each comfort room

**Target users**
Target User and How They Will Use the System?
Students
-View the list of comfort rooms and their locations.
Maintenance Staff
-Update the status of the comfort room, add a reason, and record repairs.
School Administrator
-Review the maintenance history to monitor the condition of the comfort rooms.

**Main features**
Feature and Descriptions:
Search and Filter 
-Find comfort rooms by floor, building, or status (for example, show only working CRs). 
Nearest working CR 
-Suggest the closest working comfort room based on the user's floor or building. 
Report a problem
-Students and employees can report issues such as a broken faucet, no water, or a bad smell. 
Availability
-Mark a comfort room whether it is working or under maintenance
Login with roles 
-Students, staff, and administrators see different menus.

**Technologies Used**
Language: Java
Paradigm: Object-Oriented Programming (OOP)
Tools: JDK, and any Java IDE or terminal (e.g., IntelliJ IDEA, Eclipse, VS Code)
Version control: Git and GitHub

**Team members and roles**
Garcia, Richmond Flavian E.
-Lead Programmer
Magtibay, Khryss Dilwyn S.
-Project Leader/ System/Design Lead 
Saguid, Kristine P.
-Documentation/Support Programmer

**OOP Concepts demonstrated**
Classes and Objects
-Each comfort room, stall, and urinal is stored as its own item with its own status.
Methods
-Actions such as viewing the list, changing a status, and counting working stalls each have their own set of instructions.
Encapsulation
-The status of a stall or urinal is protected so it can only be changed through the proper update process.
Inheritance
-Stalls and urinals share the same basic details, such as an ID and status. Each one builds on these with its own details. Students and staff also share basic user details.
Polymorphism
-Stalls and urinals can show their details in their own way, while the system treats them in the same way. The menu also changes depending on the type of user.
Abstraction
-The system defines what every fixture or user must have, without describing a specific one.
Exception Handling
-If a user enters an invalid choice, the system shows a message and asks again instead of stopping.
Arrays/Data Handling
-Lists are used to store comfort rooms, their stalls and urinals, and maintenance records.

**Instructions for running the program**
The user starts the system and selects their role from the main menu. A student or employee can view the comfort rooms and see how many stalls and urinals are working. Maintenance staff can choose a comfort room, select a stall or urinal, change its status, and enter a reason. The system checks the input, saves the update with the date and time, and shows the new status. The user then returns to the main menu.

Start
  ↓
Main Menu
  ↓
Select User Role
  ↓
Select Operation (View/ Update/ View History)
  ↓
Enter Information (Floor, Comfort Room, Stall or Urinal, Status, Reason)
  ↓
Validate Input
  ↓
Process Request
  ↓
Display Result
  ↓
Return to Main Menu
  ↓
Exit

**Known Limitations**
- The system does not send notifications, alarms, or messages to users or staff.
- The system does not show whether a stall is vacant or occupied.
- The system does not track water supply, cleanliness, or supplies such as soap and tissue.

**Future Improvements**
- Search and filter comfort rooms by floor, building, or status
- Suggest the nearest working comfort room based on the user's location
- Let students and employees report problems (broken faucet, no water, bad smell)
- Add login with role-based menus
- Add notifications for staff and users

**Project Contributors**
Garcia, Richmond Flavian E. – Lead Programmer
Magtibay, Khryss Dilwyn S. – Project Leader / System Design Lead
Saguid, Kristine P. – Documentation / Support Programmer
