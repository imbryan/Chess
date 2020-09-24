import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;

public class Chess implements MouseListener,ActionListener{
	Player playerOne;
	Player playerTwo;
	Piece[][] gameBoard = new Piece[8][8]; // Handles the Piece objects
	
	ArrayList<Piece> piecesOutOfPlay = new ArrayList<Piece>(); // Stores pieces out of play
	
	JFrame win;
	JPanel playPanel, sidePanel, sideContainer, outOfPlay;
	JLabel[][] checkerBoard = new JLabel[8][8]; // Handles the checkerboard
	JLabel[][] visualGameBoard = new JLabel[8][8]; // Handles the visual representation of pieces
	
	JLabel whoseTurn; // Displays the player making a move
	String turn = ""; // Stores the color of the player that is currently making a move
	int selections = 0;
	
	
	int firstI,firstJ,secondI,secondJ; // Store the indices of the player's selections
	
	ImageIcon light = new ImageIcon(getClass().getResource("/icon/light.png"));
	ImageIcon dark  = new ImageIcon(getClass().getResource("/icon/dark.png"));
	
	JLabel first = new JLabel("First tile selected");
	JLabel second = new JLabel("Second tile selected");
	
	JButton confirm = new JButton("Confirm move"), reset = new JButton("Reset move");
	
		public Chess() {
			win = new JFrame("Chess");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			randomize();
			
			playPanel = new JPanel();
			playPanel.setSize(480,480);
			sideContainer = new JPanel();
			sidePanel = new JPanel();
			outOfPlay = new JPanel();
			confirm.addActionListener(this);
			reset.addActionListener(this);
			
			
			if (playerOne.getPlayerPosition().equals("top")){
				gameSetup(playerOne.getPieces(),playerTwo.getPieces());
			}
			else{
				gameSetup(playerTwo.getPieces(),playerOne.getPieces());
			}
			
			
			playPanel.setLayout(new GridLayout(8,8));
			
			for (int i = 0; i<checkerBoard.length;i++){
				for (int j = 0; j<checkerBoard[i].length;j++){
					checkerBoard[i][j] = new JLabel();
					visualGameBoard[i][j] = new JLabel();
					checkerBoard[i][j].addMouseListener(this);
					checkerBoard[i][j].setOpaque(false);
					checkerBoard[i][j].setFocusable(true);
					
					if ((i%2==0 && j%2==0) || ((i%2!=0 && j%2!=0))){
						checkerBoard[i][j].setIcon(light); // light color
					}
					else {
						checkerBoard[i][j].setIcon(dark); // dark color
					}
					
					
					if (gameBoard[i][j]!=null){
						visualGameBoard[i][j].setIcon(gameBoard[i][j].getIcon());
					}
					
					checkerBoard[i][j].setLayout(new BorderLayout());
					checkerBoard[i][j].add(visualGameBoard[i][j]);
					
					
					playPanel.add(checkerBoard[i][j]);
				}
			}
			
			
			//sidePanel.setLayout(new GridLayout(2,2));
			
			nextTurn();
			sidePanel.add(whoseTurn);
			
			sidePanel.add(new JLabel("<html><br /></html>"));
			sidePanel.add(reset);
			sidePanel.add(confirm);
			
			outOfPlay.add(new JLabel("Pieces Out of Play:"));
			
			sideContainer.setLayout(new GridLayout(2,1));
			sideContainer.add(sidePanel);
			sideContainer.add(outOfPlay);
			
			win.setLayout(new GridLayout(2,1));
			win.add(playPanel);
			win.add(sideContainer);
			
			
			win.setSize(480,960);
			win.setVisible(true);
			
		}
	
		public void actionPerformed(ActionEvent e){
			if (reset == e.getSource()){
				resetSelections();
				
			}
			else if (confirm == e.getSource()){
				// If two valid selections have been made, proceed
				if (firstI>-1&&secondI>-1){
					
					// adds to move counter
					getPlayer(turn).moved();
					
					// flags a piece as moved
					if (gameBoard[firstI][firstJ].getHasMoved()==false)
						gameBoard[firstI][firstJ].setHasMoved(true);
					
					gameBoard[firstI][firstJ].pieceMoved();
					
					// handles normal piece capture
					if (gameBoard[secondI][secondJ]!=null&&!gameBoard[secondI][secondJ].getPieceColor().equalsIgnoreCase(turn)){
						piecesOutOfPlay.add(gameBoard[secondI][secondJ]);
						JLabel out = new JLabel();
						out.setIcon(gameBoard[secondI][secondJ].getIcon());
						outOfPlay.add(out);
					}
					
					// Flags when a pawn double jumps
					if(gameBoard[firstI][firstJ].getPieceType().equalsIgnoreCase("pawn")){
						if(secondI-2==firstI||secondI+2==firstI){
							gameBoard[firstI][firstJ].setDoubleJump(getPlayer(turn).getMoveCount());
						}
					}
					
					// handles en passant capture
					if (gameBoard[firstI][firstJ].getPieceType().equalsIgnoreCase("pawn")){
						if (enPassant()){
							if(movingPlayerPosition().equalsIgnoreCase("top")){
								piecesOutOfPlay.add(gameBoard[secondI-1][secondJ]);
								JLabel out = new JLabel();
								out.setIcon(gameBoard[secondI-1][secondJ].getIcon());
								outOfPlay.add(out);
								
								gameBoard[secondI-1][secondJ] = null;
								checkerBoard[secondI-1][secondJ].remove(visualGameBoard[secondI-1][secondJ]);
								visualGameBoard[secondI-1][secondJ] = new JLabel();
								checkerBoard[secondI-1][secondJ].add(visualGameBoard[secondI-1][secondJ]);
							}
							else{
								piecesOutOfPlay.add(gameBoard[secondI+1][secondJ]);
								JLabel out = new JLabel();
								out.setIcon(gameBoard[secondI+1][secondJ].getIcon());
								outOfPlay.add(out);
								
								gameBoard[secondI+1][secondJ] = null;
								checkerBoard[secondI+1][secondJ].remove(visualGameBoard[secondI+1][secondJ]);
								visualGameBoard[secondI+1][secondJ] = new JLabel();
								checkerBoard[secondI+1][secondJ].add(visualGameBoard[secondI+1][secondJ]);
							}
						}
					}
					
					
					// Moves piece on gameBoard array
					gameBoard[secondI][secondJ] = gameBoard[firstI][firstJ];
					gameBoard[firstI][firstJ] = null;
					
					
					// Temporarily moves visualGameBoard JLabel from checkerBoard JLabel
					checkerBoard[secondI][secondJ].remove(visualGameBoard[secondI][secondJ]);
					checkerBoard[firstI][firstJ].remove(visualGameBoard[firstI][firstJ]);
					
					// Moves JLabels on visualGameBoard array
					visualGameBoard[secondI][secondJ] = visualGameBoard[firstI][firstJ];
					visualGameBoard[firstI][firstJ] = new JLabel();
					
					// Re-adds visualGameBoard JLabels to checkerBoard array
					checkerBoard[firstI][firstJ].add(visualGameBoard[firstI][firstJ]);
					checkerBoard[secondI][secondJ].add(visualGameBoard[secondI][secondJ]);
					
					// handles promotion
					if (gameBoard[secondI][secondJ].getPieceType().equalsIgnoreCase("pawn")){
						if (isOppositeSide()){
							fix();
							
							int option = -1;
							while(option<0){
								Object[] options =  {"Rook", "Knight", "Bishop","Queen"};
								option = JOptionPane.showOptionDialog(win, "What would you like to promote your pawn to?", "Pawn Promotion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,options,options[3]);
							}
							String temp = "";
							switch(option){
							case 0:
								temp = "rook";
								break;
							case 1:
								temp = "knight";
								break;
							case 2:
								temp = "bishop";
								break;
							case 3:
								temp = "queen";
								break;
							}
							gameBoard[secondI][secondJ].promote(temp);
							checkerBoard[secondI][secondJ].remove(visualGameBoard[secondI][secondJ]);
							visualGameBoard[secondI][secondJ].setIcon(gameBoard[secondI][secondJ].getIcon());
							checkerBoard[secondI][secondJ].add(visualGameBoard[secondI][secondJ]);
						}
						
					}
					
					nextTurn();
					resetSelections();
				}
				else {
					JOptionPane.showMessageDialog(win, "Selections incomplete!");
				}
			}
		}
		
		public void mouseClicked(MouseEvent e){
			for (int i=0;i<8;i++){
				for (int j=0;j<8;j++){
					if (checkerBoard[i][j]==e.getSource()){
						if(selections==0){
							
							if (gameBoard[i][j]==null){
								JOptionPane.showMessageDialog(win, "There is no piece there!");
							}
							else if (!gameBoard[i][j].getPieceColor().equalsIgnoreCase(turn)){
								JOptionPane.showMessageDialog(win, "That is not your piece!");
							}
							else{
							sidePanel.add(first);
							selections++;
							
							firstI = i;
							firstJ = j;
							System.out.println(selections);
							
							}
							
						}
						else if (selections==1){
							if (firstI==i&&firstJ==j){
								JOptionPane.showMessageDialog(win, "Illegal Move");
							}
							else {
								sidePanel.add(second);
								selections++;
							
								secondI = i;
								secondJ = j;
								
								if (isMoveLegal()==false) {
									secondI = -1;
									secondJ = -1;
									sidePanel.remove(second);
									selections--;
								}
								System.out.println(selections);
							}
						}
						else {
							JOptionPane.showMessageDialog(win, "Too many selections");
						}
						fix();
					}
				}
			}
			

		}
		
		// Checks if the move being made is legal, used in second MouseClick
		public boolean isMoveLegal(){
			switch (gameBoard[firstI][firstJ].getPieceType()){
				case "pawn":
					// Prevents pawn from moving diagonally if there is no piece there
					if (secondJ!=firstJ&&gameBoard[secondI][secondJ]==null&&enPassant()==false){
							illegal();
							return false;
						}
					
					// Prevents pawn from moving diagonally if there is a friendly piece there
					if (secondJ!=firstJ&&gameBoard[secondI][secondJ]!=null){
						if (gameBoard[secondI][secondJ].getPieceColor().equalsIgnoreCase(turn)){
							illegal();
							return false;
						}
					}
					
					// Prevents pawn from moving sideways
					if (secondJ!=firstJ&&secondI==firstI){
						illegal();
						return false;
					}
					
					// top-side pawns
					if (movingPlayerPosition().equalsIgnoreCase("top")){
						// Checks if pawn can advance two ranks down
						if (secondI>firstI+2||(secondI>firstI+1&&gameBoard[firstI][firstJ].getHasMoved()==true)){
							illegal();
							return false;
						}
						
						// Prevents the top side pawn from moving backwards
						if (secondI<firstI){
							illegal();
							return false;
						}
						
					}
					else { //Bottom side pawns
						// Checks if pawn can advance two ranks up
						if (secondI<firstI-2||(secondI<firstI-1&&gameBoard[firstI][firstJ].getHasMoved()==true)){
							illegal();
							return false;
						}
						
						// Prevents the bottom side pawn from moving backwards
						if (secondI>firstI){
							illegal();
							return false;
						}
					}
					
					// Checks if pawn is blocked
					if (secondJ==firstJ&&gameBoard[secondI][secondJ]!=null&&!gameBoard[secondI][secondJ].getPieceColor().equalsIgnoreCase(turn)){
						illegal();
						return false;
					}
					break;
					
			}
			
			// Prevents piece from taking out own piece
			if (gameBoard[secondI][secondJ]!=null&&gameBoard[secondI][secondJ].getPieceColor().equalsIgnoreCase(turn)){
				illegal();
				return false;
			}
			
			return true; // if all checks have been passed
		}
		
		// Checks if the move is a promotion to the 8th rank
		public boolean isOppositeSide(){
			if (movingPlayerPosition().equalsIgnoreCase("top")){
				if (secondI==7){
					return true;
				}
				else {
					return false;
				}
			}
			else{
				if (secondI==0){
					return true;
				}
				else{
					return false;
				}
			}
		}
		
		// Gets the position (top or bot) of the player who's making a move
		public String movingPlayerPosition(){
			return getPlayer(turn).getPlayerPosition();
		}
		
		// Checks if there is an en passant capture
		public boolean enPassant(){
			
			if (movingPlayerPosition().equalsIgnoreCase("top")){
				if (firstI==4){
					if(checkSidesForEnemies()&&didPawnJumpRecently()){
						return true;
					}
				}
			}
			else {
				if (firstI==3){
					if(checkSidesForEnemies()&&didPawnJumpRecently()){
						return true;
					}
				}
			}
			return false;
		}
		
		// checks if adjacent pieces have doublejumped recently
		public boolean didPawnJumpRecently(){
			if (firstJ==0&&gameBoard[firstI][firstJ+1]!=null){
				if (gameBoard[firstI][firstJ+1].getDoubleJump()==getOtherPlayer().getMoveCount()){
					return true;
				}
			}
			if (firstJ==7&&gameBoard[firstI][firstJ-1]!=null){
				if (gameBoard[firstI][firstJ-1].getDoubleJump()==getOtherPlayer().getMoveCount()){
					return true;
				}
			}
			if (firstJ<7&&firstJ>0){
				if (gameBoard[firstI][firstJ+1]!=null){
					if (gameBoard[firstI][firstJ+1].getDoubleJump()==getOtherPlayer().getMoveCount()){
						return true;
					}
				}
				else if (gameBoard[firstI][firstJ-1]!=null){
					if (gameBoard[firstI][firstJ-1].getDoubleJump()==getOtherPlayer().getMoveCount()){
						return true;
					}
				}
			}
			return false;
		}
		
		// Used in en passant method
		// Checks piece's adjacent positions to determine if there is
		// a piece to capture en passant
		public boolean checkSidesForEnemies(){
			// If piece is on left-most file
			if (firstJ==0&&gameBoard[firstI][firstJ+1]!=null){
				
				if(!gameBoard[firstI][firstJ+1].getPieceType().equalsIgnoreCase("pawn"))
					return false;
				
				// If the piece is an enemy and has moved only once
				if(!gameBoard[firstI][firstJ+1].getPieceColor().equalsIgnoreCase(turn)&&gameBoard[firstI][firstJ+1].getMoves()==1)
					return true;
			}
			// If piece is on right-most file
			if (firstJ==7&&gameBoard[firstI][firstJ-1]!=null){
				
				if(!gameBoard[firstI][firstJ-1].getPieceType().equalsIgnoreCase("pawn"))
					return false;
				
				// If the piece is an enemy and has only moved once
				if(!gameBoard[firstI][firstJ-1].getPieceColor().equalsIgnoreCase(turn)&&gameBoard[firstI][firstJ-1].getMoves()==1)
					return true;
			}
			// If piece is on middle files
			if (firstJ<7&&firstJ>0) {
				// If both sides are empty
				if (gameBoard[firstI][firstJ+1]==null&&gameBoard[firstI][firstJ-1]==null){
					return false;
				}
				// If right side is occupied
				if(gameBoard[firstI][firstJ+1]!=null){
					if(!gameBoard[firstI][firstJ+1].getPieceType().equalsIgnoreCase("pawn"))
						return false;
					// If the piece is an enemy and has only moved once
					if(!gameBoard[firstI][firstJ+1].getPieceColor().equalsIgnoreCase(turn)&&gameBoard[firstI][firstJ+1].getMoves()==1)
						return true;
				}
				// If left side is occupied
				else if(gameBoard[firstI][firstJ-1]!=null){
					if(!gameBoard[firstI][firstJ-1].getPieceType().equalsIgnoreCase("pawn"))
						return false;
					
					// If the piece is an enemy and has only moved once
					if(!gameBoard[firstI][firstJ-1].getPieceColor().equalsIgnoreCase(turn)&&gameBoard[firstI][firstJ-1].getMoves()==1)
						return true;
				}
				
			}
			return false;
		}
		
		public void illegal(){
			JOptionPane.showMessageDialog(win, "Illegal Move");
		}
		
		public void resetSelections(){
			selections = 0;
			sidePanel.remove(first);
			sidePanel.remove(second);
			firstI = -1;
			firstJ = -1;
			secondI = -1;
			secondJ = -1;
			fix();
		}
		
		public void fix(){
			win.repaint();
			win.revalidate();
		}
		
		public void randomize(){
			
			if(Math.random()>0.5) {
				playerOne = new Player("Player One", "Black");
				playerTwo = new Player("Player Two", "White");
			}
			else {
				playerOne = new Player("Player One", "White");
				playerTwo = new Player("Player Two", "Black");
			}
			
			if(Math.random()>0.5){
				playerOne.setPlayerPosition("top");
				playerTwo.setPlayerPosition("bot");
			}
			else{
				playerOne.setPlayerPosition("bot");
				playerTwo.setPlayerPosition("top");
			}
			if(Math.random()>0.5)
				turn = "Black";
			else
				turn = "White";
			
			
			playerOne.assignPieces();
			playerTwo.assignPieces();
		}
		
		public void nextTurn(){
			// If no one has made a move yet
			if(getPlayer(turn).getMoveCount()==0){
				whoseTurn = new JLabel();
			}
			// Actual Next Turn
			else {
				if(turn.equalsIgnoreCase("Black")){
				turn = "White";
				}
				else {
					turn = "Black";
				}
			}
			
			whoseTurn.setText(getPlayer(turn).getPlayerName()+" ("+turn+")'s move");
			fix();
		}
		
		// Gets player whose making a move
		public Player getPlayer(String color){
			if (color.equalsIgnoreCase(playerOne.getPlayerColor()))
				return playerOne;
			return playerTwo;
			
		}
		
		public Player getOtherPlayer(){
			if (getPlayer(turn)==playerOne)
				return playerTwo;
			else
				return playerOne;
		}
		
		
		
		public void printTables(){
			System.out.println("gameBoard (Piece objects)");
			for (int i=0;i<8;i++){
				for(int j = 0;j<8;j++){
					if (gameBoard[i][j]!=null){
						System.out.print("O");
					}
					else {
						System.out.print("-");
						
					}
					if (j==7)
							System.out.print("\n");
				}
			}
			
			System.out.println("visualGameBoard (pieces' JLabels)");
			for (int i=0;i<8;i++){
				for(int j = 0;j<8;j++){
					if (visualGameBoard[i][j].getIcon()!=null){
						System.out.print("O");
					}
					else {
						System.out.print("-");
						
					}
					if (j==7)
							System.out.print("\n");
				}
			}
		}
		
		public void gameSetup(ArrayList<Piece> topPieces, ArrayList<Piece> bottomPieces){
			
				int list = 0;
				for (int i = 0; i<2; i++){
					for (int j = 0; j<8; j++){
						gameBoard[i][j] = topPieces.get(list);
						list++;
					}
				}
				
				list = 0;
				for (int i = 7; i>5; i--){
					for (int j = 0; j<8; j++){
						gameBoard[i][j] = bottomPieces.get(list);
						list++;
					}
				}
			
		}
		
		public static void main(String[] args){
			Chess game = new Chess();
			//System.out.println(System.getProperty("user.dir"));
		}

		public void mouseEntered(MouseEvent e) {
		}
		
		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

}
