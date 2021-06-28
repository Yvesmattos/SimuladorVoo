package br.nom.belo.marcio.simuladorvoo;

class Aviao implements Runnable {

	private Aeroporto aeroporto;
	private String idAviao;
	private long tempoVoo = 0;

	public Aviao(Aeroporto aeroporto, String idAviao, long tempoVoo) {
		this.aeroporto = aeroporto;
		this.idAviao = idAviao;
		this.tempoVoo = tempoVoo;
	}

	public void run() {
		try {
			Thread.sleep(tempoVoo / 2);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		try {
			decolar();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		voar();
		try {
			aterrisar();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void decolar() throws InterruptedException {

		// alterado pelo grupo
		synchronized (aeroporto) {
			aeroporto.wait();
		}
		System.out.println(idAviao + ": esperando pista...");
		String acao = idAviao + ": decolando...";
		aeroporto.esperarPistaDisponivel(acao); // Espera uma pista livre

	}

	private void voar() {
		System.out.println(idAviao + ": voando...");
		System.out.println();
		try {
			Thread.sleep(tempoVoo);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}

	private void aterrisar() throws InterruptedException {

		// alterado pelo grupo
		synchronized (aeroporto) {
			aeroporto.wait();
		}
		System.out.println(idAviao + ": esperando pista...");
		String acao = idAviao + ": aterissando...";
		aeroporto.esperarPistaDisponivel(acao); // Espera uma pista livre
		System.out.println();
	}
}

class Aeroporto implements Runnable {

	private boolean temPistaDisponivel = true;
	private String nomeAeroporto;
	private boolean voando = true;

	public Aeroporto(String nomeAeroporto) {
		this.nomeAeroporto = nomeAeroporto;
	}

	// alterado pelo grupo
	public synchronized void setVoando(boolean b) {
		voando = b;
	}

	public synchronized void esperarPistaDisponivel(String acao) {
		System.out.println(acao);
	}

	public synchronized void mudarEstadoPistaDisponivel() {
		// Inverte o estado da pista.
		temPistaDisponivel = !temPistaDisponivel;
		System.out.println(nomeAeroporto + " tem pista disponível: " + (temPistaDisponivel == true ? "Sim" : "Não"));
		// Notifica a mudanca de estado para quem estiver esperando.
		if (temPistaDisponivel) {
			this.notify();
		}

	}

	public void run() {
		System.out.println("Rodando aeroporto " + nomeAeroporto);

		// alterado pelo grupo
		while (voando) {
			try {
				mudarEstadoPistaDisponivel();
				// Coloca a thread aeroporto dormindo por um tempo de 0 a 5s
				Thread.sleep((int) (Math.random() * 5000));
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}

}

/*
 * Simulador de voo com threads
 */
public final class SimuladorVoo {

	public static void main(String[] args) {

		System.out.println("Rodando simulador de voo.");

		// Constroi aeroporto e inicia sua execucao.
		// NÃO MEXER NESSE TRECHO
		Aeroporto santosDumont = new Aeroporto("Santos Dumont");
		Thread threadAeroporto = new Thread(santosDumont);

		// Constrói aviao e inicia sua execucao.
		// NÃO MEXER NESSE TRECHO
		Aviao aviao14bis = new Aviao(santosDumont, "Avião 14BIS", 10000);
		Thread thread14bis = new Thread(aviao14bis);

		// alterado pelo grupo
		Aviao dassault = new Aviao(santosDumont, "Avião Dassault Mirage III", 8500);
		Thread threadDassault = new Thread(dassault);

		Aviao embraerKc = new Aviao(santosDumont, "Avião Embraer KC-390", 12000);
		Thread threadEmbraerKc = new Thread(embraerKc);

		Aviao demoiselle = new Aviao(santosDumont, "Avião Santos Dummont Demoiselle", 15000);
		Thread threadDemoiselle = new Thread(demoiselle);

		Aviao wright = new Aviao(santosDumont, "Avião Wright Flyer", 20000);
		Thread threadWright = new Thread(wright);

		threadAeroporto.start();
		thread14bis.start();
		threadDassault.start();
		threadEmbraerKc.start();
		threadDemoiselle.start();
		threadWright.start();

		try {
			// alterado pelo grupo
			thread14bis.join();
			threadDassault.join();
			threadEmbraerKc.join();
			threadDemoiselle.join();
			threadWright.join();
			santosDumont.setVoando(false);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		System.out.println("Terminando thread principal.");

	}

}