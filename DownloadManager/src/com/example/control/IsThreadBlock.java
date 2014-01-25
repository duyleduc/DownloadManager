package com.example.control;

public class IsThreadBlock {
		private String description;
		private boolean isBlocked;

		public IsThreadBlock(String description) {
			super();
			isBlocked = false;
			this.description = description;
		}

		public void setBlocked() {
			this.isBlocked = true;
		}

		public boolean getIsBlocked() {
			return this.isBlocked;
		}

		public void setNotBlocked() {
			this.isBlocked = false;
		}

	}