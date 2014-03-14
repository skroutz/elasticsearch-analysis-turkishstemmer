package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NounState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;

public class NounTransitionTest {

  private NounTransition transition;

  @BeforeClass
  public void setup() {
    transition = new NounTransition(NounState.A,
        NounState.B, "aword", NounSuffix.S1, "anotherWord", false);
  }

  @DataProvider(name = "transitions")
  public Object[][] transitionsProvider() {
    NounTransition similarTransition = new NounTransition(
        NounState.A, NounState.B, "someWord",
        NounSuffix.S2, "someOtherWord", false);

    NounTransition differentTransition = new NounTransition(
        NounState.C, NounState.D, "someWord",
        NounSuffix.S2, "someOtherWord", false);

    List<NounTransition> initialTransitions = new ArrayList<NounTransition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<NounTransition> similarTransitions = new ArrayList<NounTransition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "transitions")
  public void similarTransitions(List<NounTransition> transitions,
      List<NounTransition> similarTransitions) {

    Assert.assertEquals(transition.similarTransitions(transitions),
        similarTransitions);
  }
}