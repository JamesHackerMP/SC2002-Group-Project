# Presentation Script
## Characters
- Manager Martha
  - An HDB Manager who has an active project Pineapple House.
- Manager Matthew
  - An HDB Manager who has an active Project Krusty Krab
- Office Oswald
  - A 20-year-old married HDB Officer who has not been assigned to any Project Krusty Krabnd is going to be 21 years old today. 
- Applicant Austin
  - A 35-year-old single man who has just married today
## Scene0: I attempt to run
1.	The code cannot run, since now Martha has two active Project Krusty Krab & Pineapple House
2.	Change to the manager of the Project Krusty Krab to Matthew
## Scene1: Austin checks projects before he gets married
1.	Austin logins with invalid NRIC__A1231231N
2.	Austin logins with invalid Password__S1231231N__pass
3.	Austin logins__S1231231N__password
4.	Austin checks his profile to check his name, NRIC, age and married status__1
5.	Austin view available projects__4__1
6.	Austin logs out__0__0
## Scene2: Oswald checks projects before he is 21 years old
1.	Oswald logins__S1231231D__password
2.	Austin checks his profile to check his name, NRIC, age and married status__1
3.	Oswald views available projects__5__1
4.	Oswald logs out__0__0
## Scene3: I update the data
1.	I change Austin’s married status to “Married”
2.	I change Oswald’s age to “21”
## Scene4: Austin applies projects after he gets married
1.	Austin logins__S1231231N__password
2.	Austin checks his profile to check his name, NRIC, age and married status__1
3.	Austin changes his password to “MarriedMe”__2__MarriedMe
4.	I check the applicant csv
5.	Austin views available projects__5__1
6.	Austin alters filter to Neighborhood of Bikini Bottom__0__3__Bikini Bottom
7.	Austin view available projects (now Mickey Mouse Clubhouse disappear)__0__4__1
8.	Austin applies the Project Krusty Krab 3-Room__2__1__1
9.	Austin views his application__3
10.	Austin withdraws his application for Project Krusty Krab__4__1
11.	Austin views his application__3
12.	Austin applies the Project Pineapple House 2-Room__2__2__1
13.	Austin views his application__3
14.	Austin enquiries about the Project Pineapple House__5__1__2__”Too expensive!”
15.	Austin checks his enquiry__2
16.	Austin deletes his enquiry__4__1
17.	Austin checks his enquiry__2
18.	Austin logs out__0__0
## Scene5: Oswald applies the same project to Austin
1.	Oswald logins__S1231231D__ MarriedMe 
2.	Oswald views available projects__5__1
3.	Oswald applies the Project Pineapple House 3-Room__2__1__1
4.	Oswald attempts to register the Project Pineapple House but cannot see__0__4__1
5.	Oswald enquiries about the project he applies__0__5__1__3__”?”
6.	Oswald checks his enquiry__2
7.	Oswald edits his enquiry__3__1__ “I only want 3-Room, otherwise please reject”
8.	Oswald checks his enquiry__2
9.	Oswald logs out__0__0
## Scene6: Martha processes Austin’s and Oswald’s applications
1.	Martha logins__S1231231A__password
2.	Martha updated Project Pineapple House’s available 3-Room to 0__4__2__1__Enter__Enter__0
3.	I check project csv
4.	Martha replies to Oswald’s enquiry__9__1__”As you wish”
5.	Martha rejects Oswald’s application__
6.	Martha approves Austin’s application
7.	Martha logs out__0__0
## Scene7: Oswald registers Project Pineapple House
1.	Oswald logins__S1231231D__password
2.	Oswald checks the enquiry__5__5__2
3.	Oswald views his application__0__3
4.	Oswald enquires about Project Krusty Krab__5__1__1
5.	Oswald register the Project Pineapple House__0__0__4__1__3
6.	Oswald views his registration status__2
7.	Oswald attempts to apply the Project Pineapple House but cannot see__0__5__2__0
8.	Oswald logs out__0__0
## Scene8: Martha processes Oswald’s registration
1.	Martha logins__S1231231A__password
2.	Martha check Oswald’s enquiry (but cannot reply)__4__5
3.	Martha approves Oswald’s registration__3__1__1__1
4.	I check the project CSV (to see Oswald)
5.	Martha attempts to create a new project (cannot even get in)__2__1
6.	Martha toggles Project Pineapple House to Hidden__4__1
7.	Martha attempts to creates a new project with the same name (cannot)__1__”Pineapple House”
8.	Martha creates a new active Project__1__ “Sleeping Beauty Castle”__”Disneyland”__1__100000__0__2025/04/01__2025/05/01__3
9.	I check the project CSV (to see new project)
10.	Martha attempts to toggle Project Pineapple House to visible (cannot)__4__1
11.	Martha logs out__0__0__0.
## Scene9: Austin contacts Oswald to book flat
1.	Austin logins__S1231231N__MarriedMe
2.	Austin checks his filter setting__3
3.	Austin views his application (can see Oswald)__4__3
4.	Austin enquiries about Project Pineapple House__5__1__3__“Hi Oswald, please assist me to book 2-Room, thanks!
5.	Austin logs out__0__0
## Scene10: Matthew toggles visibility
1.	Matthew logins__S1231231W__password
2.	Matthew views all projects__4__1
3.	Matthew alters the filter to his name__0__3__5__”Matthew”
4.	Matthew views all projects__0__4__1
5.	Matthew toggles Project Krusty Krab to Hidden__4__1
6.	Matthew logs out__0__0
## Scene11: Oswald books flat for Austin
1.	Oswald logins__S1231231D__password
2.	Oswald views his project (even though Martha has turned it to Hidden)__4__2
3.	Oswald replies to Austin__5__1__”Sure things”
4.	Oswald books the 2-Room flat for Austin__3__1__1
5.	Oswald views his project__2
6.	Oswald generates the receipt for Austin__4__1
7.	Oswald view available projects (cannot see Project Pineapple House)__0__5__1
8.	Oswald applies the project Mickey Mouse Clubhouse__2__2__1
9.	Oswald logs out__0__0
## Scene12: Austin checks his booked flat
1.	Austin logins__S1231231N__MarriedMe
2.	Austin views his application__4__3
3.	Austin logs out__0__0
## Scene13: Martha generates a report
1.	Martha logins__S1231231A__password
2.	Martha generates a report by married__4__6__4__1
3.	Martha generates a report by age__6__7__30__40
4.	Martha logs out__0__0