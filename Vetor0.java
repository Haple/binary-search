import java.util.Arrays;
import java.lang.reflect.*;
// Fazer uma terceira versão usando paralelismo
// Runtime.getRuntime().avaibleProcessors()
public class Vetor0 <X extends Comparable<X>>{

	private Object[] vetor;
	private int qtd=0;

	/**
	 * Constrói um novo vetor com uma capacidade fixa.
	 */
	public Vetor0(int capacidade) throws Exception{
		if(capacidade<=0)
			throw new Exception("Capacidade invalida");
		this.vetor=new Object[capacidade];
	}

	/**
	 * Construtor de cópia.
	 */
	public Vetor0(Vetor0 modelo) throws Exception{
		if(modelo==null)
			throw new Exception("Vetor inválido");
		this.qtd = modelo.qtd;
		this.vetor = new Object[modelo.vetor.length];
		for(int i=0; i<this.qtd; i++)
			this.vetor[i]=meuCloneDeX((X)modelo.vetor[i]);
	}

	public Object clone(){
		Vetor0 clone = null;
		try{
			clone = new Vetor0(this);
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

	public int buscaBinaria(X x) throws Exception{
		if(x==null)
			throw new Exception("Item inválido");
		if(this.qtd==0)
			throw new Exception("Vetor vazio");
		return buscaBinaria(x,0,this.qtd-1);
	}

	private int buscaBinaria(X x, int inicio, int fim){
		if(fim<inicio)
			return -1;
		int meio = inicio + (fim-inicio)/2;
		X itemDoMeio = (X)this.vetor[meio];
		if(x.equals(itemDoMeio))
			return meio;
		else if(x.compareTo(itemDoMeio) < 0){
			int primeiroMeio = (inicio+meio)/2;
			return buscaBinaria(x,inicio,meio-1);
		}else{
			int segundoMeio = (meio+fim)/2;
			return buscaBinaria(x,meio+1,fim);
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
		if(this.qtd<=1)
			throw new Exception("Nada para ordenar");
		this.sort(0,this.qtd-1);
	}
	// Usar paralelismo entre as duas chamadas das metades
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
		Vetor0 outro = (Vetor0)obj;
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

