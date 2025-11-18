package com.effectello;

public class CloneResult {

	private final int totalCount;
	private final int succeededCount;
	private final int failureCount;

	public CloneResult(){
		this(0, 0, 0);
	}

	public CloneResult(int totalCount, int succeededCount, int failureCount) {
		this.totalCount = totalCount;
		this.succeededCount = succeededCount;
		this.failureCount = failureCount;
	}

	public int totalCount() {
		return this.totalCount;
	}

	public int succeededCount() {
		return succeededCount;
	}

	public int failureCount() {
		return this.failureCount;
	}

	CloneResult merge(final CloneResult result) {
		return new CloneResult(
			this.totalCount + result.totalCount(),
			this.succeededCount + result.succeededCount(),
			this.failureCount + result.failureCount());
	}
}
