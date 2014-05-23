package com.diligencedojo.boxready;

public class Movement {
	String mMovementName;
	Integer mReps;
	String mName;
	String[] mWod;
	Boolean mStart;
	Integer mTotMoveCount;

	@Override
	public String toString() {
		return mMovementName;
	}

	public void setMovementName(String movementName) {
		mMovementName = movementName;
	}

	public String getMovementName() {
		return mMovementName;
	}

	public void setReps(Integer reps) {
		mReps = reps;
	}

	public Integer getReps() {
		return mReps;
	}

	String[] movementNames = { "Push Ups", "Pull Ups", "Burpees", "Air Squats",
			"HSPU Progression", "Mountain Climbers", "Run", "Jump Rope",
			"Walking Lunges", "Box Jumps" };

	public String getMovList(Integer position) {
		return movementNames[position];
	}

	String[] reps = { "40", "20", "20", "40", "10", "40", "1/4 mile", "75",
			"40", "30" };

	public String getReps(Integer position) {
		return reps[position];
	}

	String[] diffWODstyles = { "3 MOVEMENTS FOR 3 ROUNDS", "21-15-9",
			"MAX REPS" };

	String[] wodScoring = {
			"  Your score is\n   the time it takes do the list 3 times.",
			"\n\tRound 1: Do 21 reps per movement\n"
					+ "\tRound 2: Do 15 reps per movement\n"
					+ "\tRound 3: Do   9 reps per movement",
			"Do as many reps of the\n  movement as possible in 7 minutes." };

	public void setWodStyle(Integer totMovCount) {
		mTotMoveCount = totMovCount;
	}

	public Integer getWodStyle() {
		return mTotMoveCount;
	}

	String[] chooseMovInstr = { "Select 3 movements.", "Select 2 movements",
			"Choose " + "1 movement" };

	Integer[] numMoves = { 3, 2, 1 };

	String[] movDescriptions = { // pushups //
			"- Lie face down on the floor with\n   your hands slightly wider than\n   shoulder width.\n"
					+ "- Push upwards until your arms\n   are completely extended,\n   moving the body as one unit.\n"
					+ "- Keep your body flat and lower\n   yourself to the floor.\n- Repeat.",
			// pullups //
			"- Begin by hanging from the bar\n   with arms fully extended.\n- Pull body upward until your chin\n"
					+ "   is above the bar.\n- Lower body until arms are fully\n   extended.\n- Repeat.",
			// burpees //
			"- Start from the standing position \n   and move to a push up "
					+ "position.\n- At the bottom position the chest \n   and hips must touch the ground.\n- From this position, "
					+ "return to a \n   standing posititon and jump and\n   clap hands above head.\n- The jump must be a minimum of\n   "
					+ "6 " + "inches off the ground.\n- Repeat.",
			// squats //
			"- Start from the standing position\n   with feet slightly outside the\n   shoulders.\n"
					+ "- Keep your knees directly over\n   your toes throughout the\n   movement.\n"
					+ "- Lower body into a squat, keeping\n   the chest up and eyes forward.\n- The butt should be at, or "
					+ "below\n   parallel with the knees at the\n   bottom of the movment.\n- Return to the standing position.\n- "
					+ "Repeat.",
			// hspu //
			"Handstand Push Up Progression",
			// mountain climbers //
			"- Start in the push up position.\n"
					+ "- Bring the right knee to the right elbow and then replace right foot to the floor.\n"
					+ "- Repeat with the left knee and replace the left foot to the floor.\n"
					+ "- Alternate movement with right and left knees.\n",
			// run //
			"Run",
			// jump rope //
			"Jump Rope",
			// walking lunges //
			"- Start from the standing position\n"
					+ "- Step far forward with one leg\n   while simultaneously lifting up\n   onto the ball of the back foot.\n"
					+ "- Keeping your chest up and\n   shoulders back, bend the knees\n   and drop your hips downwards\n   straight to the ground.\n"
					+ "- Press up with your front leg and\n   bring the back foot forward.\n"
					+ "- Step forward with the opposite\n   leg and repeat the lunge.\n",
			// box jumps //
			"- Stand in front of the box with\n   feet directly under the hips.\n"
					+ "- Lower yourself into the jumping\n   position. Keep your head up and\n   back straight.\n"
					+ "- Explosively jump from the\n   crouched position.  Swing your\n   arms to make the jump easier.\n"
					+ "- Land softly on the box absorbing\n   the impact with your legs.\n"
					+ "- Stand up straight, flattening at\n   the knee and hips.\n"
					+ "- Jump backwards off the box, or\n   step down and repeat.\n" };

	public String getDescrList(Integer position) {
		return movDescriptions[position];
	}

	// Uri[] videos = {
	// Uri.parse("http://www.youtube.com/watch?feature=player_detailpage&v=2mcqHMNoH_4"),
	// Uri.parse("http://www.youtube.com/watch?v=XSMgkNc17A4"),
	// Uri.parse("http://www.youtube.com/watch?v=7Do3zchsH0I"),
	// Uri.parse("http://www.youtube.com/watch?v=_-6Agfc88sY"),
	// Uri.parse("http://www.youtube.com/watch?v=cbqHHcTHKdo"),
	// Uri.parse("http://www.youtube.com/watch?v=1J4hRICVjRo"),
	// Uri.parse("http://www.youtube.com/watch?feature=player_detailpage&v=wRkeBVMQSgg"),
	// Uri.parse("http://www.youtube.com/watch?feature=player_detailpage&v=LsWui2L_r2c"),
	// Uri.parse("http://www.youtube.com/watch?v=n_xR8h2eCBI"),
	// Uri.parse("http://www.youtube.com/watch?feature=player_detailpage&v=IxrzCG_7FH4")
	// };
	//
	// public Uri getVideo(Integer position) {
	// return videos[position];
	// }

}
