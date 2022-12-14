package com.gotriva.nlp.antifa.element;

/** An interface to represent a check interaction capable element. */
public interface Checkable extends Interactable {

  /** Perform a check on this element, if it is unchecked. */
  void check();
}
