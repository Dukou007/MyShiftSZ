package com.pax.tms.download.util;

import java.util.ArrayList;
import java.util.List;

public class TerminalSpliterator {

	private List<String> terminals;
	private List<String> batch;
	private int index = 0;

	public TerminalSpliterator(List<String> terminals) {
		this.terminals = terminals;
		this.batch = new ArrayList<>(terminals.size() > 200 ? 200 : terminals.size());
	}

	public boolean nextBatch() {
		if (!batch.isEmpty()) {
			batch.clear();
		}
		if (index >= terminals.size()) {
			return false;
		}

		int count = 0;
		for (; index < terminals.size() && count < 200; index++, count++) {
			batch.add(terminals.get(index));
		}
		return true;
	}

	public int batchSize() {
		return batch.size();
	}

	public String get(int i) {
		return batch.get(i);
	}
}
