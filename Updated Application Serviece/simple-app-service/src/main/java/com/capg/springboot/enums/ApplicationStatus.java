package com.capg.springboot.enums;

// This enum holds all possible statuses for a job application
// Status can only move FORWARD - never backward
// REJECTED is the final state - no changes allowed after this

public enum ApplicationStatus {

    APPLIED,        // seeker just submitted the application
    UNDER_REVIEW,   // recruiter opened and is looking at it
    SHORTLISTED,    // recruiter liked the profile
    REJECTED        // final state - cannot change after this

}
