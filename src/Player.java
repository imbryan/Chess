import java.util.*;

public class Player {
	private ArrayList<Piece> playersPieces = new ArrayList<Piece>();
	private String playerColor;
	private String playerName;
	private String playerPosition;
	private int moveCount;
	
	public Player(String name,String color){
		playerName  = name;
		playerColor = color;
		moveCount = 0;
		
	}
	
	public void assignPieces(){
		boolean flag = false;
		for (int i=0; i<16; i++){
			if (i<=7) {
				
				if (i==0||i==7)
					playersPieces.add(new Piece(playerColor,"rook"));
				else if (i==1||i==6)
					playersPieces.add(new Piece(playerColor,"knight"));
				else if (i==2||i==5)
					playersPieces.add(new Piece(playerColor, "bishop"));
				else if (i==3){
					String kingOrQueen="";
					if ((playerPosition.equalsIgnoreCase("top")&&playerColor.equalsIgnoreCase("white"))||(playerPosition.equalsIgnoreCase("bot")&&playerColor.equalsIgnoreCase("black"))){
						kingOrQueen="king";
						flag = true;
					}
					else{
						kingOrQueen="queen";
					}
						
					playersPieces.add(new Piece(playerColor, kingOrQueen));
				}
				else if (i==4){
					String kingOrQueen;
					//if (playerPosition.equalsIgnoreCase("bot")&&playerColor.equalsIgnoreCase("white"))
				
					if (flag == true){
						kingOrQueen="queen";
						
					}
					else{
						kingOrQueen="king";
					}
					
					playersPieces.add(new Piece(playerColor, kingOrQueen));
				}
					
			}
			else if (i>7)
				playersPieces.add(new Piece(playerColor,"pawn"));
		}
	}
	
	public void moved(){
		moveCount++;
	}
	
	public int getMoveCount(){
		return moveCount;
	}
	
	public String getPlayerName(){
		return playerName;
	}
	
	public String getPlayerPosition(){
		return playerPosition;
	}
	
	public void setPlayerPosition(String pos){
		playerPosition = pos;
	}
	
	public ArrayList<Piece> getPieces(){
		for(Piece piece : playersPieces){
			//System.out.println(piece.getPieceType());
		}
		return playersPieces;
	}
	
	public String getPlayerColor(){
		return playerColor;
	}
}
