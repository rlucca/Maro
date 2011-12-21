
import maro.wrapper.OwlApi;
import maro.wrapper.Dumper;

public class TestOwlApi {
// getFunctor retorna como esta na ontologia Capitalizado
// getFunctorComplete retorna formato jason nao capitalizado
	static public void main(String[] args) {
		try {
			OwlApi oa = new OwlApi();
			oa.loadOntologyFromFile("../onto/nina.owl");

			for (OwlApi.Relation r : oa.getConcepts()) {
				System.out.println(r);
			}
			System.out.println("--");
			for (OwlApi.Relation r : oa.getObjectProperties()) {
				System.out.println(r);
			}
			System.out.println("--");
			for (OwlApi.Relation r : oa.getDataProperties()) {
				System.out.println(r);
			}

			System.out.println("--");
			System.out.print("isRelevant(1,cogumelo): ");
			if (oa.isRelevant(1,"cogumelo") == false)
				System.out.println("OK");
			else
				System.out.println("ERROR");

			System.out.print("isRelevant(1,hasThreshold): ");
			if (oa.isRelevant(1,"hasThreshold") == false)
				System.out.println("OK");
			else
				System.out.println("ERROR");

			System.out.print("isRelevant(2,hasThreshold): ");
			if (oa.isRelevant(2,"hasThreshold") == true)
				System.out.println("OK");
			else
				System.out.println("ERROR");

			//System.out.println("--");
			//for (Dumper d : oa) {
			//	System.out.println(d);
			//}

			System.out.println("--");
			for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasThreshold")) {
				System.out.println(d);
			}
			if (oa.getCandidatesByFunctorAndArity(2,"hasThreshold").size() == 22) {
				System.out.println("oa.getCandidatesByFunctorAndArity Data OK");
			} else {
				System.out.println("oa.getCandidatesByFunctorAndArity Data ERROR");
			}

			System.out.println("--");
			oa.add(2,"hasThreshold", new String[] { "shushu", "10" }, null);
			boolean insertedData = false;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasThreshold")) {
				if (d.getFunctorComplete().equals("hasThreshold") && d.getArity() == 2) {
					if (d.getTermAsString(0).equals("shushu") && d.getTermAsString(1).equals("10"))
					{
						insertedData= true;
						break;
					}
				}
			}
			if (insertedData) {
				System.out.println("oa.add Data OK");
			} else {
				System.out.println("oa.add Data ERROR");
			}

			System.out.println("--");
			oa.remove(2,"hasThreshold", new String[] { "shushu", "10" }, null);
			boolean removedData = true;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasThreshold")) {
				if (d.getFunctorComplete().equals("hasThreshold") && d.getArity() == 2) {
					if (d.getTermAsString(0).equals("shushu") && d.getTermAsString(1).equals("10"))
					{
						removedData = false;
						break;
					}
				}
			}
			if (removedData && insertedData == true
					&& oa.getCandidatesByFunctorAndArity(2,"hasThreshold").size() == 22) {
				// remove soh 1, os outros tem que ser mantidos
				System.out.println("oa.remove Data OK");
			} else {
				System.out.println("oa.remove Data ERROR");
			}

			System.out.println("--");
			oa.remove(2,"hasThreshold", null, null);
			if (oa.getCandidatesByFunctorAndArity(2,"hasThreshold").size() == 0)
				System.out.println("abolish Data OK");
			else {
				System.out.println("abolish Data ERROR");
				for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasThreshold")) {
					System.out.println(d);
				}
			}

			System.out.println("--");
			oa.add(2,"hasSetup", new String[] { "shushu", "abacate" }, null);
			boolean insertedObject = false;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasSetup")) {
				if (d.getFunctorComplete().equals("hasSetup") && d.getArity() == 2) {
					if (d.getTermAsString(0).equals("shushu") && d.getTermAsString(1).equals("abacate"))
					{
						insertedObject= true;
						break;
					}
				}
			}
			if (insertedObject) {
				System.out.println("oa.add Object OK");
			} else {
				System.out.println("oa.add Object ERROR");
			}

			System.out.println("--");
			oa.remove(2,"hasSetup", new String[] { "shushu", "abacate" }, null);
			boolean removedObject = true;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasSetup")) {
				if (d.getFunctorComplete().equals("hasSetup") && d.getArity() == 2) {
					if (d.getTermAsString(0).equals("shushu") && d.getTermAsString(1).equals("abacate"))
					{
						removedObject = false;
						break;
					}
				}
			}
			if (removedObject && insertedObject == true
					&& oa.getCandidatesByFunctorAndArity(2,"hasSetup").size() == 22) {
				// remove soh 1, os outros tem que ser mantidos
				System.out.println("oa.remove Object OK");
			} else {
				System.out.println("oa.remove Object ERROR");
			}

			System.out.println("--");
			oa.remove(2,"hasSetup", null, null);
			if (oa.getCandidatesByFunctorAndArity(2,"hasSetup").size() == 0)
				System.out.println("abolish Object OK");
			else {
				System.out.println("abolish Object ERROR");
				for (Dumper d : oa.getCandidatesByFunctorAndArity(2,"hasSetup")) {
					System.out.println(d);
				}
			}

			System.out.println("--");
			oa.add(1,"agent", new String[] { "shushu" }, null);
			boolean insertedInstance = false;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(1,"agent")) {
				if (d.getFunctorComplete().equals("agent") && d.getArity() == 1) {
					if (d.getTermAsString(0).equals("shushu"))
					{
						insertedInstance= true;
						break;
					}
				}
			}
			if (insertedInstance) {
				System.out.println("oa.add Instance OK");
			} else {
				System.out.println("oa.add Instance ERROR");
				for (Dumper d : oa.getCandidatesByFunctorAndArity(1,"agent")) {
					System.out.println(d);
				}
			}

			System.out.println("--");
			oa.remove(1,"agent", new String[] { "shushu" }, null);
			boolean removedInstance = true;
			for (Dumper d : oa.getCandidatesByFunctorAndArity(1,"agent")) {
				if (d.getFunctorComplete().equals("agent") && d.getArity() == 1) {
					if (d.getTermAsString(0).equals("shushu"))
					{
						removedInstance = false;
						break;
					}
				}
			}
			if (removedInstance && insertedInstance == true
					&& oa.getCandidatesByFunctorAndArity(1,"agent").size() == 4) {
				// remove soh 1, os outros tem que ser mantidos
				System.out.println("oa.remove Instance OK");
			} else {
				System.out.println("oa.remove Instance ERROR");
			}

			System.out.println("--");
			oa.remove(1,"agent", null, null);
			if (oa.getCandidatesByFunctorAndArity(1,"agent").size() == 0)
				System.out.println("abolish Instance OK");
			else {
				System.out.println("abolish Instance ERROR");
				for (Dumper d : oa.getCandidatesByFunctorAndArity(1,"agent")) {
					System.out.println(d);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
