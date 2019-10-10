import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Teste{

	private static int TAMANHO_VETOR = 10;
	private static int ITEM = TAMANHO_VETOR-1;

	public static void main(String[] args){
		boolean thread = false;
		if(args.length == 0){
			System.out.println("Parâmetro 1: números de elementos no vetor de teste");
			System.out.println("Parâmetro 2 (opcional): --thread");
			return;
		}
		TAMANHO_VETOR = Integer.parseInt(args[0]);
		ITEM = TAMANHO_VETOR-1;
		if(args.length == 2 && args[1].toLowerCase().equals("--thread")){
			thread = true;
		}
		if(thread)
			testeVetorComParalelismo();
		else
			testeVetorSemParalelismo();
	}

	private static void testeVetorComParalelismo(){
		try{
			System.out.println("Vetor com Threads");
			Vetor<Integer> vet = new Vetor<Integer>(TAMANHO_VETOR);
			for(int i=0;i<TAMANHO_VETOR;i++){
				vet.adicione(i);
			}
			System.out.println("Vetor criado com " + TAMANHO_VETOR + " posições");
			System.out.println("Cronometrando a busca binária...");
			LocalDateTime inicio = LocalDateTime.now();
			int index = vet.buscaBinaria(ITEM);
			LocalDateTime fim = LocalDateTime.now();
			System.out.println("Milisegundos: " + ChronoUnit.MILLIS.between(inicio,fim));
			System.out.println("Posição do item "+ITEM+": " + index);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void testeVetorSemParalelismo(){
		try{
			System.out.println("Vetor sem Threads");
			Vetor0<Integer> vet = new Vetor0<Integer>(TAMANHO_VETOR);
			for(int i=0;i<TAMANHO_VETOR;i++){
				vet.adicione(i);
			}
			System.out.println("Vetor criado com " + TAMANHO_VETOR + " posições");
			System.out.println("Cronometrando a busca binária...");
			LocalDateTime inicio = LocalDateTime.now();
			int index = vet.buscaBinaria(ITEM);
			LocalDateTime fim = LocalDateTime.now();
			System.out.println("Milisegundos: " + ChronoUnit.MILLIS.between(inicio,fim));
			System.out.println("Posição do item "+ITEM+": " + index);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
