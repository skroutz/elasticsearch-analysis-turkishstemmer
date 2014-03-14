package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NominalVerbState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;

public class NominalVerbTransitionTest {

  private NominalVerbTransition transition;

  @BeforeClass
  public void setup() {
    transition = new NominalVerbTransition(NominalVerbState.A,
        NominalVerbState.B, "aword", NominalVerbSuffix.S1, "anotherWord", false);
  }

  @DataProvider(name = "transitions")
  public Object[][] transitionsProvider() {
    NominalVerbTransition similarTransition = new NominalVerbTransition(
        NominalVerbState.A, NominalVerbState.B, "someWord",
        NominalVerbSuffix.S2, "someOtherWord", false);

    NominalVerbTransition differentTransition = new NominalVerbTransition(
        NominalVerbState.C, NominalVerbState.D, "someWord",
        NominalVerbSuffix.S2, "someOtherWord", false);

    List<NominalVerbTransition> initialTransitions = new ArrayList<NominalVerbTransition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<NominalVerbTransition> similarTransitions = new ArrayList<NominalVerbTransition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "transitions")
  public void similarTransitions(List<NominalVerbTransition> transitions,
      List<NominalVerbTransition> similarTransitions) {

    Assert.assertEquals(transition.similarTransitions(transitions),
        similarTransitions);
  }
}
