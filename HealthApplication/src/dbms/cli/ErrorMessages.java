package dbms.cli;

public enum ErrorMessages {

	
	UNIQUE_CONSTRAINT("ORA-00001");
	
	private String errorMessage;
	
	 ErrorMessages(String errorMessage){
		this.errorMessage = errorMessage;
	}

	@Override
	 public String toString(){
		return errorMessage; 
	 }
	
	
}

