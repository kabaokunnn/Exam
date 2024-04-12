package scoremanager.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.Student;
import dao.StudentDao;
import tool.Action;

public class StudentListAction extends Action {
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSesion();
		Teacher teacher = (Teacher)session.getAttribute("user");
	
		String entYearStr="";
		String classNum = "";
		String isAttendStr = "";
		int entYear = 0;
		boolean isAttend = false;
		List<Student> students = null;
		LocalDate todaysData = LocalData.now();
		int year = todaysDate.getYear();
		StudentDao sDao = new StudentDao();
		ClassNumDao cNumDao = new ClassNumDao();
		Map<String, String> errors = new HashMap<>();
		entYearStr = req.getParameter("f1");
		classNum = req.getParameter("f2");
		isAttendStr = req.getParameter("f3");
		
		List<String> list = cNumDao.filter(teacher.getSchool());
		
		if (entYear != 0 && !classNum.equals("0")) {
			students = sDao.filter(teacher.getSchool(), entYear, classNum, isAttend)
		
		} else if (entYear != 0 && classNum.equals("0")) {
			students = sDao.filter(teacher.getSchool(), entYear, classNum, isAttend);
			
		} else if (entYear != 0 && classNum == null || entYear ==  0 && classNum.equals("0")) {
			students = sDao.filter(teacher.getSchool(), entYear, classNum, isAttend);
			
		} else {
			errors.put("f1", "クラスを指定する場合は入学年度も指定してください");
			req.setAttribute("errors", errors);
			students = sDao.filter(teacher.getSchool(), isAttend);
		}
		if (entYearStr != null) {
			entYear = Integer.parseInt(entYearStr);
			
		}
		List<Integer> entYearSet = new ArrayList<>();
		
		for (int i =  year - 10; i < year + 1; i++) {
			entYearSet.add(i);
		}
		req.setAttribute("f1", entYear);
		
		req.setAttribute("f2", classNum);
		
		if (isAttendStr != null) {
			isAttend = true;
			req.setAttribute("f3", isAttendStr);
		}
		
		req.setAttribute("students", students);
		
		req.setAttribute("class_num_set", list);
		req.setAttribute("ent_year_set", entYearSet);
		
		req.getRequestDispatcher("student_list.jsp").forward(req, res);
		
		private String baseSql = "select * from student where school_cd=? ";
		private List<Student> list = new ArryList<>();
		try {
			while (rSet.next()) {
				Student student = new Student();
				
				student.setNo(rSet.getString("no"));
				student.setName(rSet.getString("name"));
				student.setEntYear(rSet.getInt("ent_year"));
				student.setClassNum(rSet.getString("class_num"));
				student.setAttend(rSet.getBoolean("is_attend"));
				student.setSchool(school);
				
				list.add(student);
			}
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	public List<Student> filter(School school, int entYear, String classNum, boolean isAttend) throws Exception {
		List<Student> list = new ArrayList<>();
		
		Connection connection = getConnection();
		
		PreparedStatement statement = null;
		
		ResultSet rSet = null;
		
		String condeition ="and ent_year=? and class_num=?";
		
		String order = " order by no asc";
		
		String condeitionIsAttend = "";
		
		if (isAttend) {
			conditionIsAttend = "and is_attend=true";
		}
		
		try {
			statement = connection.prepareStatement(baseSql + condition + conditionIsAttend + order);
			
			statement.setString(1, school.getCd());
			
			statement.setInt(2, entYear);
			
			statement.setString(3, classNum);
			
			rSet = statement.executeQuery();
			
			list = postFilter(rSet, school);
		} catch (Exception e) {
			throw e;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqle) {
					throw sqle;
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqle) {
					throw sqle;
				}

			} 
		}
		return list;
	}
	public List<Student> filter(School school, int entYear, boolean isAttend) throws Exception {
		
	}
}