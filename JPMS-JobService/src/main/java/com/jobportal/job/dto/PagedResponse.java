package com.jobportal.job.dto;

import java.util.List;

public class PagedResponse {
	
	private List<JobResponse> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;

    
    public PagedResponse() {
    	
    }

	public PagedResponse(List<JobResponse> content, int currentPage, long totalElements, int totalPages) {
		super();
		this.content = content;
		this.currentPage = currentPage;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<JobResponse> getContent() {
		return content;
	}


	public void setContent(List<JobResponse> content) {
		this.content = content;
	}


	public int getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}


	public long getTotalElements() {
		return totalElements;
	}


	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}


	public int getTotalPages() {
		return totalPages;
	}


	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
    
}
