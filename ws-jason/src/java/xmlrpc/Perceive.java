package maro.xmlrpc;

import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.LinkedList;
import java.util.List;

/* The perceive class should control the data that go in and go out of Jason platform.

methods public: dump(serialize data) and undump(receive a serialize data and unserialize)

*/
public class Perceive {
	static public List<Object> dump(List<Literal> ll) {
		List<Object> ret;

		if (ll == null) return null;

		ret = new LinkedList<Object>();
		for (Literal l: ll) {
			Object s = dumpLiteral(l);
			if (s != null)
				ret.add(s); // TODO pressuposto que sempre da certo
		}
		return ret;
	}

	static public List<Literal> undump(Object[] lo) {
		List<Literal> ret;

		if (lo == null) return null;

		ret = new LinkedList<Literal>();
		for (Object o: lo) {
			String str = undumpLiteral(o);
			if (str != null) {
				Literal li = Literal.parseLiteral(str);
				if (li != null) {
					ret.add(li);
				}
			}
		}
		return ret;
	}

	static public Object dumpLiteral(Literal l) {
		List<Object> las;
		List<String> lts;
		List<Term> lts_raw;
		ListTerm las_raw;
		String functor_complete;

		if (l == null) return null;

		lts_raw = l.getTerms();
		las_raw = l.getAnnots();

		lts = new LinkedList<String>();
		if (lts_raw != null) {
			for (Term t: lts_raw) {
				lts.add( t.toString() );
			}
		}

		las = new LinkedList<Object>();
		if (las_raw != null) {
			for (Term t: las_raw) {
				las.add( dumpLiteral((Literal) t) );
			}
		}

		if (l.negated()) {
			functor_complete = new String("~" + l.getFunctor());
		} else {
			functor_complete = new String(      l.getFunctor());
		}
		return new Object[] {
								functor_complete,
								lts.toArray(),
								las.toArray()
				};
	}

	static public String undumpLiteral(Object o) {
		String ret;

		if (o == null) return new String("");
		if (!(o instanceof Object[]) || ((Object[])o).length != 3) {
			return null; // Error
		}

		Object [] oa   = (Object[]) o;

		if (!(oa[0] instanceof String)
				|| !(oa[1] instanceof Object[])
				|| !(oa[2] instanceof Object[])) {
			return null; // Error
		}

		String functor = (String)   oa[0];
		Object [] lts  = (Object[]) oa[1];
		Object [] lms  = (Object[]) oa[2];

		ret = new String(functor);

		if (lts.length > 0) {
			ret = ret + "(";

			boolean first = true;
			for (Object lt: lts) {
				if (first == true) {
					ret = ret + ((String) lt);
					first = false;
				} else {
					ret = ret + ", " + ((String) lt);
				}
			}

			ret = ret + ")";
		}

		if (lms.length > 0) {
			ret = ret + "[";

			boolean first = true;
			for (Object m: lms) {
				if (first == true) {
					ret = ret + undumpLiteral(m);
					first = false;
				} else {
					ret = ret + ", " + undumpLiteral(m);
				}
			}

			ret = ret + "]";
		}
		
		return ret;
	}
}
