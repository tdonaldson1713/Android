package com.donaldson.gradetracker;

import java.util.ArrayList;

public class GradePointCalculation {
	ArrayList<GPA> gpa_scale = new ArrayList<GPA>();
	
	public GradePointCalculation() {
		gpa_scale.add(new GPA("A", 4.0));
		gpa_scale.add(new GPA("A-", 3.7));
		gpa_scale.add(new GPA("B+", 3.3));
		gpa_scale.add(new GPA("B", 3.0));
		gpa_scale.add(new GPA("B-", 2.7));
		gpa_scale.add(new GPA("C+", 2.3));
		gpa_scale.add(new GPA("C", 2.0));
		gpa_scale.add(new GPA("C-", 1.7));
		gpa_scale.add(new GPA("D+", 1.3));
		gpa_scale.add(new GPA("D", 1.0));
		gpa_scale.add(new GPA("D-", 0.7));
		gpa_scale.add(new GPA("F", 0.0));
	}
	
	public GPA getGPA(String scale) {
		boolean found = false;
		int pos = 0;
		for (int a = 0; a < gpa_scale.size(); a++) {
			if (gpa_scale.get(a).scale.equals(scale)) {
				found = true;
				pos = a;
				break;
			}
		}
		
		if (found) {
			return gpa_scale.get(pos);
		} else {
			return null;
		}
	}
}
