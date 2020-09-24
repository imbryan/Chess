import javax.swing.*;

public class Piece {
	private String pieceColor;
	private String pieceType;
	private ImageIcon icon;
	private boolean isInPlay;
	private boolean hasMoved;
	private boolean promoted;
	private int moves;
	private int doubleJump;
	
	
	public Piece(String pC, String pT){
		pieceColor = pC;
		pieceType  = pT;
		setIcon(pieceColor,pieceType);
		isInPlay = true;
		hasMoved = false;
		promoted = false;
		moves = 0;
		
	}
	
	public void setIcon(String color, String type){
		icon = new ImageIcon(getClass().getResource("/icon/"+type+"_"+color+".png"));
	}
	public String getPieceColor(){
		return pieceColor;
	}
	public String getPieceType(){
		return pieceType;
	}
	public ImageIcon getIcon(){
		return icon;
	}
	public boolean getIsInPlay(){
		return isInPlay;
	}
	public void setPlayFlag(boolean val){
		isInPlay = val;
	}
	public boolean getHasMoved(){
		return hasMoved;
	}
	public void setHasMoved(boolean val){
		hasMoved = val;
	}
	public boolean getPromoted(){
		return promoted;
	}
	public void setPromoted(boolean val){
		promoted = val;
	}
	public void promote(String newType){
		pieceType = newType;
		setIcon(pieceColor,pieceType);
		setPromoted(true);
	}
	public void pieceMoved(){
		moves++;
	}
	public int getMoves(){
		return moves;
	}
	public void setDoubleJump(int val){
		doubleJump = val;
	}
	public int getDoubleJump(){
		return doubleJump;
	}
	
	
}
