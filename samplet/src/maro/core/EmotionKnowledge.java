package maro.core;

// my
import maro.wrapper.Dumper;
import maro.wrapper.OwlApi;

//java only
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

public class EmotionKnowledge {

    protected FeelingsThreshold ft;
    protected OwlApi oaw = null;
    protected String filename;
    protected Emotion emotion;

    public EmotionKnowledge(String fileName) throws Exception {
        filename = fileName;

        oaw = new OwlApi();
        oaw.loadOntologyFromFile(filename);

        Set<String> allEmotions;
        allEmotions = oaw.getAllEmotions();
        if (allEmotions == null || allEmotions.isEmpty()) {
            throw new Exception("Fail on load ontology data");
        }

        emotion = new Emotion();
        emotion.setEmotions(allEmotions);

        ft = new FeelingsThreshold();
    }

    protected boolean changeBelief(Dumper dumper, boolean isAdd) {
        String functor;
        String[] terms;
        Dumper[] annots;
        int arity;

        if (dumper == null) {
            return false;
        }

        terms = dumper.getTerms();
        arity = dumper.getArity();
        functor = dumper.getFunctorComplete();

        if (!oaw.isRelevant(arity, functor)) {
            return false;
        }

        annots = dumper.getAnnots();

        if (isAdd) {
            return oaw.add(arity, functor, terms, annots);
        }
        //else
        return oaw.remove(arity, functor, terms, annots);
    }

    public boolean add(Dumper dumper) {
        return changeBelief(dumper, true);
    }

    public boolean remove(Dumper dumper) {
        return changeBelief(dumper, false);
    }

    public Iterator<Dumper> getCandidatesByFunctorAndArity(String functor, int arity) {
        if (functor.equals("sameAs") || functor.equals("~sameAs")) {
            arity = 0;
        }

        if (!oaw.isRelevant(arity, functor)) {
            return null;
        }

        return oaw.getCandidatesByFunctorAndArity(arity, functor);
    }

    public Iterator<Dumper> iterator() {
        return oaw.iterator();
    }
    boolean ignoreFlag = false;

    public void summarize(String agentName, int step) {
        if (ft.isLoaded(emotion, agentName, oaw) == true
                && ft.isActive() == false) {
            if (ignoreFlag == false) {
                System.out.println("Threshold of emotions are inconsistent ou fault, ignoring emotions...");
                ignoreFlag = true;
            }
            return; // nao temos threshold pq perder tempo?
        }

        for (String s : emotion.getEmotions()) {
            HashMap<Integer, Integer> e = emotion.getAllValences(s, agentName);
            Integer ret = oaw.summaryOf(agentName, s, step, e);
            if (ret == null) {
                continue;
            }
            emotion.setValence(s, agentName, step, ret);
        }
    }

    public Set<String> feelings(String agentName, int step) {
        Set<String> feelingStrLit = new java.util.HashSet<String>();

        if (ft.isActive() == false) {
            return feelingStrLit;
        }

        for (String s : emotion.getEmotions()) {
            Integer valence = emotion.getValence(s, agentName, step);
            Integer minimum = ft.getThreshold(s);
            int feeling = (valence != null) ? valence - minimum : 0;

            if (feeling > 0) {
                feelingStrLit.add("feeling(" + s + ", " + feeling + ")");
            }
        }

        return feelingStrLit;
    }

    public void dumpData() {
        if (System.getenv("emotionKnowledgeDebug") == null) {
            return;
        }

        try {
            oaw.dumpOntology("/tmp/baka.owl");
        } catch (Exception e) {
            System.err.println("Error dumping ontology");
            e.printStackTrace();
            System.exit(29);
        }
    }
}
