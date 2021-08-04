package application;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		ChessMatch chessMach = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();

		while (!chessMach.isCheckMate()) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMach, captured);

				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);
				
				boolean[][] possibleMoves = chessMach.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMach.getPieces(), possibleMoves);

				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc);

				ChessPiece capturedPiece = 
					chessMach.performChessMove(source, target);
				
				if(capturedPiece != null) {
					captured.add(capturedPiece);
				}
				
				if(chessMach.getPromoted() != null) {
					System.out.print("Enter piece for promotion (B/N/R/Q): ");
					String type = sc.nextLine().toUpperCase();
					
					while(!type.equals("B")
							&&!type.equals("N")
							&&!type.equals("R")
							&&!type.equals("Q")) {
						System.out.print(
							"Invalid value!"
							+ "Enter piece for promotion (B/N/R/Q): "
						);
						type = sc.nextLine().toUpperCase();
					}
					chessMach.replacePromotedPiece(type);
				}
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.clearScreen();
		UI.printMatch(chessMach, captured);
	}

}
