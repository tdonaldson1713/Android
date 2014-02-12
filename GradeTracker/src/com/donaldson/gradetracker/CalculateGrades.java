package com.donaldson.gradetracker;



public class CalculateGrades {

	public static double calculate_percentage(double earned, double possible, double percentage) {
		double grade = ((earned / possible) * percentage) * 100;
		return grade; 
	}
	
	public static double calculate_points(double earned, double possible) {
		double grade = (earned / possible) * 100;
		return grade;
	}
	
	public static double calculate_running_percentage(double earned, double possible, double percentage) {
		double grade = (earned / possible) * percentage;
		return grade;
	}
	
	public static double calculate_running_points(double earned, double possible) {
		double grade = (earned / possible);
		return grade;
	}
	
	public static double calculate_future_points(double earned, double possible, double desired, double desired_possible) {
		if (desired > 1) {
			desired /= 100;
		}
		
		double grade = (desired * (possible + desired_possible)) - earned;
		return grade;
	}
	
	public static double calculate_future_percentage(double points_category, double earned_category, double percent_category, 
			double desired_percentage, double total_percentage, double total_points) {	
		double front = (points_category / percent_category);
		double middle = ((desired_percentage / 100) * total_percentage) - total_points;
		double end = earned_category;

		return (front * middle) - end;
	}
	
	public static double calculate_scaled_gpa(double sum_scaled_gpa, int num_credits) {
		return sum_scaled_gpa / num_credits;
	}
	
	public static double calculate_non_scaled_gpa(double sum_gpa, int num_classes) {
		return sum_gpa / num_classes;
	}
}
