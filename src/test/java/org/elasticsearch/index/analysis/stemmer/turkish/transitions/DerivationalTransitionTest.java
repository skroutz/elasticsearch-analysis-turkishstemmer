package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.DerivationalState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;

public class DerivationalTransitionTest {

  private DerivationalTransition transition;

  @BeforeClass
  public void setup() {
    transition = new DerivationalTransition(DerivationalState.A,
        DerivationalState.B, "aword", DerivationalSuffix.S1, "anotherWord", false);
  }

  @DataProvider(name = "transitions")
  public Object[][] transitionsProvider() {
    DerivationalTransition similarTransition = new DerivationalTransition(
        DerivationalState.A, DerivationalState.B, "someWord",
        DerivationalSuffix.S1, "someOtherWord", false);

    DerivationalTransition differentTransition = new DerivationalTransition(
        DerivationalState.A, null, "someWord",
        DerivationalSuffix.S1, "someOtherWord", false);

    List<DerivationalTransition> initialTransitions = new ArrayList<DerivationalTransition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<DerivationalTransition> similarTransitions = new ArrayList<DerivationalTransition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "transitions")
  public void similarTransitions(List<DerivationalTransition> transitions,
      List<DerivationalTransition> similarTransitions) {

    Assert.assertEquals(transition.similarTransitions(transitions),
        similarTransitions);
  }
}