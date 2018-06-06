public class ArvoreB<Key extends Comparable<Key>, Value> {
	// Máximo de filhos por nó = M-1
	private static int quantidadeDeFilhosMaximos = 4;

	private No topoDaArvore; // Topo da árvore
	private int alturaDaArvore; // Altura da árvore
	private int qtdNos; // número de nós da árvore

	// Nós da árvore
	private static final class No {
		private int qtdFilhos; // quantidade de filhos
		private Entry[] filhos = new Entry[quantidadeDeFilhosMaximos]; // lista de filhos

		// cria um nó com k filhos
		private No(int k) {
			qtdFilhos = k;
		}
	}

	// Nós internos: usa somente chave e próximo
	// Nós externos: usa somente chave e valor
	private static class Entry {
		private Comparable chave;
		private final Object valor;
		private No proximo;

		public Entry(Comparable chave, Object valor, No proximo) {
			this.chave = chave;
			this.valor = valor;
			this.proximo = proximo;
		}
	}

	/**
	 * Inicializa árvore vazia
	 */
	public ArvoreB(int quantidadeDeFilhosMaximosParam) {
		quantidadeDeFilhosMaximos = quantidadeDeFilhosMaximosParam < 2 ? quantidadeDeFilhosMaximos : quantidadeDeFilhosMaximosParam;
		topoDaArvore = new No(0);
	}

	public boolean ehVazia() {
		return qtdeDeNosDaArvore() == 0;
	}

	/**
	 * 
	 * @return quantidade de nós da árvore
	 */
	public int qtdeDeNosDaArvore() {
		return qtdNos;
	}

	public int getAlturaDaArvore() {
		return alturaDaArvore;
	}

	/**
	 * Returns the value associated with the given key.
	 *
	 * @param chave
	 * @return valor associado à chave
	 */
	public Value get(Key chave) {
		if (chave == null)
			throw new IllegalArgumentException("chave nula");
		return busca(topoDaArvore, chave, alturaDaArvore);
	}

	private Value busca(No no, Key chave, int altura) {
		Entry[] nosFilhos = no.filhos;

		// nó externo
		if (altura == 0) {
			for (int j = 0; j < no.qtdFilhos; j++) {
				if (eq(chave, nosFilhos[j].chave))
					return (Value) nosFilhos[j].valor;
			}
		}

		// no interno
		else {
			for (int j = 0; j < no.qtdFilhos; j++) {
				if (j + 1 == no.qtdFilhos || less(chave, nosFilhos[j + 1].chave))
					return busca(nosFilhos[j].proximo, chave, altura - 1);
			}
		}
		return null;
	}

	/**
	 * Insere valor na tabela
	 *
	 * @param chave
	 * @param valor
	 */
	public void put(Key chave, Value valor) {
		if (chave == null)
			throw new IllegalArgumentException("chave nula");
		No no = insereNaArvore(topoDaArvore, chave, valor, alturaDaArvore);
		qtdNos++;
		if (no == null)
			return;

		// precisa dividir o topo
		No novoNo = new No(2);
		novoNo.filhos[0] = new Entry(topoDaArvore.filhos[0].chave, null, topoDaArvore);
		novoNo.filhos[1] = new Entry(no.filhos[0].chave, null, no);
		topoDaArvore = novoNo;
		alturaDaArvore++;
	}

	private No insereNaArvore(No no, Key chave, Value valor, int altura) {
		int j;
		Entry estruturaDeNos = new Entry(chave, valor, null);

		// Nó externo
		if (altura == 0) {
			for (j = 0; j < no.qtdFilhos; j++) {
				if (less(chave, no.filhos[j].chave))
					break;
			}
		}

		// Nó interno
		else {
			for (j = 0; j < no.qtdFilhos; j++) {
				if ((j + 1 == no.qtdFilhos) || less(chave, no.filhos[j + 1].chave)) {
					No noInserido = insereNaArvore(no.filhos[j++].proximo, chave, valor, altura - 1);
					if (noInserido == null)
						return null;
					estruturaDeNos.chave = noInserido.filhos[0].chave;
					estruturaDeNos.proximo = noInserido;
					break;
				}
			}
		}

		for (int i = no.qtdFilhos; i > j; i--)
			no.filhos[i] = no.filhos[i - 1];
		no.filhos[j] = estruturaDeNos;
		no.qtdFilhos++;
		if (no.qtdFilhos < quantidadeDeFilhosMaximos)
			return null;
		else
			return divideNoAoMeio(no);
	}

	private No divideNoAoMeio(No noParaDividir) {
		No no = new No(quantidadeDeFilhosMaximos / 2);
		noParaDividir.qtdFilhos = quantidadeDeFilhosMaximos / 2;
		for (int j = 0; j < quantidadeDeFilhosMaximos / 2; j++)
			no.filhos[j] = noParaDividir.filhos[quantidadeDeFilhosMaximos / 2 + j];
		return no;
	}

	public String toString() {
		return toString(topoDaArvore, alturaDaArvore, "") + "\n";
	}

	private String toString(No h, int ht, String indent) {
		StringBuilder valores = new StringBuilder();
		Entry[] children = h.filhos;

		if (ht == 0) {
			for (int j = 0; j < h.qtdFilhos; j++) {
				valores.append(((Pedido)children[j].valor).toString() + "\n");
			}
		} else {
			for (int j = 0; j < h.qtdFilhos; j++) {
				valores.append(toString(children[j].proximo, ht - 1, ""));
			}
		}
		return valores.toString();
	}
	
	public String toStringOld() {
		return toStringOld(topoDaArvore, alturaDaArvore, "") + "\n";
	}
	
	private String toStringOld(No h, int ht, String indent) {
		StringBuilder s = new StringBuilder();
		Entry[] children = h.filhos;

		if (ht == 0) {
			for (int j = 0; j < h.qtdFilhos; j++) {
				s.append(indent + children[j].chave + " " + children[j].valor + "\n");
			}
		} else {
			for (int j = 0; j < h.qtdFilhos; j++) {
				if (j > 0)
					s.append(indent + "(" + children[j].chave + ")\n");
				s.append(toStringOld(children[j].proximo, ht - 1, indent + "     "));
			}
		}
		return s.toString();
	}

	private boolean less(Comparable k1, Comparable k2) {
		return k1.compareTo(k2) < 0;
	}

	private boolean eq(Comparable k1, Comparable k2) {
		return k1.compareTo(k2) == 0;
	}
}