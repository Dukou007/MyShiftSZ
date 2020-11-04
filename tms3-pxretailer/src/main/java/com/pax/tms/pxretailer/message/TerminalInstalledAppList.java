package com.pax.tms.pxretailer.message;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TerminalInstalledAppList extends ArrayList<TerminalInstalledApp> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8757213791440956553L;

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof List))
			return false;
		List<?> list = (List<?>) o;
		if (this.size() != list.size()) {
			return false;
		}

		ListIterator<TerminalInstalledApp> e1 = listIterator();

		return equal(e1, (List<TerminalInstalledApp>) o);

	}

	private boolean equal(ListIterator<TerminalInstalledApp> e1, List<TerminalInstalledApp> o) {
		boolean isEqual = true;
		while (e1.hasNext()) {

			TerminalInstalledApp o1 = e1.next();
			ListIterator<TerminalInstalledApp> e2 = ((List<TerminalInstalledApp>) o).listIterator();
			isEqual = isMatcher(o1, e2);
			// 未找到立即返回,减少匹配次数
			if (!isEqual) {
				return false;
			}

		}

		return isEqual;
	}

	private boolean isMatcher(TerminalInstalledApp o1, ListIterator<TerminalInstalledApp> e2) {
		if (o1 == null) {
			return false;
		}
		while (e2.hasNext()) {
			TerminalInstalledApp o2 = e2.next();
			if (o2 == null) {
				continue;
			}

			if (o1.equals(o2)) {
				return true;
			}
		}
		return false;
	}

}
