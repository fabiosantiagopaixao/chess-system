package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	
	public ChessMatch() {
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		this.board = new Board(8, 8);
		this.initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean isCheck() {
		return check;
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = 
			new ChessPiece[board.getRows()][board.getColumns()];
		
		for (int i=0; i<board.getRows(); i++) {
			for (int j=0; j<board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition){
		Position position = sourcePosition.toPosition();
		this.validateSourcePosition(position);
		return this.board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(
		ChessPosition sourcePosition,
		ChessPosition targetPosition
	) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		this.validateSourcePosition(source);
		this.validateTargetPosition(source, target);
		
		Piece capturedPiece = makeMove(source, target);
		
		if (this.testCheck(this.currentPlayer)) {
			this.undoMove(source, target, capturedPiece);
			throw new ChessException("You cant put yoursef in check");
		}
		
		this.check = 
			(this.testCheck(opponent(this.currentPlayer))) ? true : false;
		
		this.nextTurn();
		
		return (ChessPiece)capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		Piece p = this.board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		this.board.placePiece(p, target);
		
		if(capturedPiece != null) {
			this.piecesOnTheBoard.remove(capturedPiece);
			this.capturedPieces.add(capturedPiece);
		}
		
		return capturedPiece;
	}
	
	private void undoMove(
		Position source,
		Position target,
		Piece capturedPiece
	) {
		Piece p = this.board.removePiece(target);
		this.board.placePiece(p, source);
		
		if(capturedPiece != null) {
			this.board.placePiece(capturedPiece, target);
			
			this.capturedPieces.remove(capturedPiece);
			this.piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		if(this.currentPlayer 
			!= ((ChessPiece) this.board.piece(position)).getColor()) {
			throw new ChessException(
				"The chosen piece is not yours"
			);
		}
		if(!this.board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException(
				"There is no posible moves for the chosen piece"
			);
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!this.board.piece(source).possibleMove(target)) {
			throw new ChessException(
				"The chosen piece can't move to target position"
			);
		}
	}
	
	private void nextTurn() {
		this.turn++;
		this.currentPlayer = 
			(this.currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE; 
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = this.piecesOnTheBoard.stream().filter(
			x -> ((ChessPiece)x).getColor() == color
		).collect(Collectors.toList());
		
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece) p;
			}
		}
		
		throw new IllegalStateException(
			"There is no " + color + " King on the board"
		);
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = this.king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = this.piecesOnTheBoard.stream().filter(
				x -> ((ChessPiece)x).getColor() == this.opponent(color)
			).collect(Collectors.toList());
		
		for(Piece p : opponentPieces) {
			boolean[][] possibleMoves = p.possibleMoves();
			if(possibleMoves[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		
		return false;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		// White pieces
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

		// Black pieces
        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
