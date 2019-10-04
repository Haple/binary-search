import java.util.Arrays;
import java.lang.reflect.*;
//Considerando um vetor já ordenado,
//dê a cada thread um pedaço desse
//vetor e faça cada uma fazer a busca
//de um item em cada parte

public class Vetor <X extends Comparable<X>>{

	private Thread[] threadsSort;
	private Thread[] threadsMerge;
	private Thread[] threadsBusca;
	private int numProcessadores;
	private int qtdThreadSort=0;
	private int qtdThreadMerge=0;

	private Object[] vetor;
	private int qtd=0;

	/**
	 * Constrói um novo vetor com uma capacidade fixa.
	 */
	public Vetor(int capacidade) throws Exception{
		if(capacidade<=0)
			throw new Exception("Capacidade invalida");
		this.vetor=new Object[capacidade];
		this.numProcessadores=Runtime.getRuntime().availableProcessors()-1;
		this.threadsSort=new Thread[this.numProcessadores];
		this.threadsMerge=new Thread[this.numProcessadores];
		this.threadsBusca=new Thread[this.numProcessadores];
	}

	/**
	 * Construtor de cópia.
	 */
	public Vetor(Vetor modelo) throws Exception{
		if(modelo==null)
			throw new Exception("Vetor inválido");
		this.qtd = modelo.qtd;
		this.vetor = new Object[modelo.vetor.length];
		for(int i=0; i<this.qtd; i++)
			this.vetor[i]=meuCloneDeX((X)modelo.vetor[i]);
	}

	public Object clone(){
		Vetor clone = null;
		try{
			clone = new Vetor(this);
		}catch(Exception e){}
		return clone;
	}

	private X meuCloneDeX(X x){
		X ret=null;
		try{
			Class<?> classe = x.getClass();
			Method metodo = classe.getMethod("clone");
			ret=(X)metodo.invoke(x);
		}catch(Exception e){}
		return ret;
	}

	/**
	 * Adiciona um item no vetor.
	 */
	public void adicione(X x) throws Exception{
		if(x==null)
			throw new Exception("Valor ausente");
		if(this.qtd==this.vetor.length)
			throw new Exception("Nao cabe");
		if(x instanceof Cloneable)
			this.vetor[this.qtd]=meuCloneDeX(x);
		else
			this.vetor[this.qtd]=x;
		this.qtd++;
	}

	public int busca(X x) throws Exception{
		if(x==null)
			throw new Exception("Item inválido");
		if(this.qtd==0)
			throw new Exception("Vetor vazio");
		final int[] resultado = new int[this.numProcessadores];
		int tamParte = this.qtd/this.numProcessadores;
		int posAtual=0;
		int i=0;
		if(tamParte > 0){
			for(; i<this.numProcessadores-1; i++,posAtual+=tamParte){
				buscaBinariaParalela(i,x, posAtual,posAtual+tamParte-1,resultado);
			}
		}
		buscaBinariaParalela(i,x,posAtual,this.qtd-1,resultado);
		System.out.println("Aguardando as threads terminarem...");
		esperaFimDasThreads(this.threadsBusca);
		System.out.println("Terminaram. Começando verificação dos resultados");
		for(int j=0; j<resultado.length;j++)
			if(resultado[j] > -1) return resultado[j];
		return -1;
	}

	private void buscaBinariaParalela(final int id, 
					final X x, 
					final int inicio, 
					final int fim, 
					final int[] resultado){
		Thread t = new Thread(()->{
			resultado[id] = buscaBinaria(x,inicio,fim);
		});
		this.threadsBusca[id] = t;
		t.start();
		System.out.println("Iniciada thread " + id);
	}

	private int buscaBinaria(X x, int inicio, int fim){
		int meio = inicio + (fim-inicio)/2;
		X itemDoMeio = (X)this.vetor[meio];
		if(x.equals(itemDoMeio))
			return meio;
		if(fim<inicio)
			return -1;
		else if(x.compareTo(itemDoMeio) < 0){
			int primeiroMeio = (inicio+meio)/2;
			return buscaBinaria(x,inicio,meio-1);
		}else{
			int segundoMeio = (meio+fim)/2;
			return buscaBinaria(x,meio+1,fim);
		}
	}

	/**
	 * Remove um elemento específico do vetor.
	 * Caso tenham itens repetidos, o primeiro item
	 * encontrado é o que será removido.
	 */
	public void remova(X x) throws Exception{
		if(x==null)
			throw new Exception("Item inválido");
		if(this.qtd==0)
			throw new Exception("Vetor vazio");
		int i;
		for(i=0; i<this.qtd; i++){
			if(this.vetor[i].equals(x)){
				this.remova(i);
			}
		}
	}

	/**
	 * Remove um item de uma posição específica do vetor.
	 */
	public void remova(int posicao) throws Exception{
		if(posicao >= this.qtd || posicao < 0)
			throw new Exception("Posicao inválida");
		for(int i = posicao; i < this.qtd-1; i++){
			this.vetor[i] = this.vetor[i+1];
		}
		this.vetor[this.qtd-1] = null;
		this.qtd--;
	}

	/**
	 * Ordena o vetor usando o algoritmo Merge Sort.
	 */
	public void mergeSort() throws Exception{
		this.qtdThreadSort=0;
		this.qtdThreadMerge=0;
		if(this.qtd<=1)
			throw new Exception("Nada para ordenar");
		int tamParte = this.qtd/this.numProcessadores;
		int posAtual=0;
		if(tamParte > 0){
			for(int i=0; i<this.numProcessadores-1; i++,posAtual+=tamParte){
				sortParalelo(posAtual,posAtual+tamParte-1);
			}
		}
		sortParalelo(posAtual,this.qtd-1);
		esperaFimDasThreads(this.threadsSort);
		posAtual=0;
		for(int i=0; i<this.numProcessadores-1;i++){
			mergeParalelo(0,posAtual+tamParte-1,posAtual+tamParte,posAtual+(tamParte*2)-1);
			posAtual+=tamParte;
		}
		posAtual+=tamParte;
		merge(0,posAtual-1, posAtual,this.qtd-1);
		//esperaFimDasThreads(this.threadsMerge);
	}

	private void esperaFimDasThreads(Thread[] threads) throws Exception{
		for(int i=0; i<threads.length; i++)
			threads[i].join();
	}

	private void sortParalelo(final int inicio, final int fim){
		Thread t = new Thread(()->{
			this.sort(inicio,fim);
		});
		this.threadsSort[this.qtdThreadSort++] = t;
		t.start();
	}

	private void mergeParalelo(final int ini1, final int fim1, final int ini2, final int fim2){
		Thread t = new Thread(()->{
			this.merge(ini1,fim1,ini2,fim2);
		});
		this.threadsMerge[this.qtdThreadMerge++] = t;
		t.start();
	}

	private  void sort(int inicio, int fim){
		int tamanho = fim - inicio + 1;
		if(tamanho==1) return;
		int metade = inicio + tamanho/2;
		sort(inicio, metade-1);
		sort(metade, fim);
		merge(inicio,metade-1,metade,fim);
	}

	private void merge(int ini1, int fim1, int ini2, int fim2){
		int qtd1=fim1-ini1+1, qtd2=fim2-ini2+1, qtd=qtd1+qtd2;
                Object[] vet = new Object[qtd];
		int i=0; int j=ini1; int k=ini2;
		while(j<=fim1 && k<=fim2){
			if(((X)this.vetor[j]).compareTo((X)this.vetor[k]) > 0)
				vet[i++] = this.vetor[k++];
			else if (((X)this.vetor[j]).compareTo((X)this.vetor[k]) < 0)
				vet[i++] = this.vetor[j++];
			else {
	 			vet[i++] = this.vetor[j++];
				vet[i++] = this.vetor[k++];
			}
		}
		while(j<=fim1)
			vet[i++]=this.vetor[j++];
		while(k<=fim2)
			vet[i++]=this.vetor[k++];
		for(i=0;i<qtd;i++)
			this.vetor[ini1+i]=vet[i];
	}

	public int hashCode(){
		int primo = 31;
		int resultado = 1;
		resultado += primo * resultado + this.qtd;
		for(int i=0; i<this.qtd; i++)
			resultado += primo * resultado 
				+ ((this.vetor[i] == null) ? 0 : this.vetor[i].hashCode());
		return resultado;
	}

	/**
	 * Verifica se outro objeto é igual a ele.
	 * @param obj o objeto a ser comparado.
	 */
	public boolean equals(Object obj){
		if(obj==null)
			return false;
		if(this==obj)
			return true;
		if(this.getClass() != obj.getClass())
			return false;
		Vetor outro = (Vetor)obj;
		if(this.qtd != outro.qtd)
			return false;
		for(int i=0; i<this.qtd; i++)
			if(!this.vetor[i].equals(outro.vetor[i]))
				return false;
		return true;
	}

	@Override
	public String toString() {
		String retorno = "[";
		for(int i=0; i<this.qtd-1;i++){
			retorno+=this.vetor[i].toString()+", ";
		}
		retorno+=this.vetor[this.qtd-1].toString()+"]";
		return retorno;
	}
}
