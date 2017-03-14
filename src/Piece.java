class Piece{
	PType type;
	int row;
	int column;
	
	Piece(PType type, int row, int column){
		this.type=type;
		this.row=row;
		this.column=column;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Piece))
			return false;
		Piece other = (Piece) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}