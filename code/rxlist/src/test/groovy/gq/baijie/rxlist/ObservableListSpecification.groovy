package gq.baijie.rxlist

import spock.lang.Specification

class ObservableListSpecification extends Specification {

  private List internalList
  private ObservableList observableList

  def setup() {
    internalList = new LinkedList()
    observableList = ObservableList.create(internalList)
  }

  def cleanup() {
    observableList = null
    internalList = null
  }


  def "list() has right elements"() {
    given:
    def assertList = {observableList.list().equals(internalList)}

    expect:
    observableList.list().isEmpty()
    assertList()

    when:
    observableList.add('test0')
    then:
    observableList.list().size() == 1
    assertList()

    when:
    internalList.add('test1')
    then:
    observableList.list().size() == 2
    assertList()

    when:
    observableList.remove('test0')
    then:
    observableList.list().size() == 1
    assertList()

    when:
    internalList.clear()
    then:
    observableList.list().isEmpty()
    assertList()
  }

  def "shouldn't modify list()"() {
    //and:'cannot add'
    when:
    observableList.list().add('test')
    then:
    thrown(UnsupportedOperationException)

    and: 'cannot remove'
    when:
    observableList.add('test')
    observableList.list().remove('test')
    then:
    observableList.list().size() == 1
    observableList.list().get(0) == 'test'
    thrown(UnsupportedOperationException)

    and: 'cannot clear'
    when:
    observableList.list().clear()
    then:
    observableList.list().size() == 1
    observableList.list().get(0) == 'test'
    thrown(UnsupportedOperationException)

    and: 'cannot set'
    when:
    assert observableList.list().size() == 1
    assert observableList.list().get(0) == 'test'
    observableList.list().set(0, 'changed test')
    then:
    observableList.list().size() == 1
    observableList.list().get(0) == 'test'
    thrown(UnsupportedOperationException)
  }

  def "basic function run as expected"() {
    expect:
    observableList.list().isEmpty()

    when:
    observableList.add('string0')
    then:
    observableList.list().with {
      size() == 1 &&
      get(0) == 'string0'
    }

    when:
    observableList.add(0, 'new string0')
    then:
    observableList.list().with {
      size() == 2 &&
      get(0) == 'new string0' &&
      get(1) == 'string0'
    }

    when:
    observableList.set(1, 'old string0 at index 1')
    then:
    observableList.list().with {
      size() == 2 &&
      get(0) == 'new string0' &&
      get(1) == 'old string0 at index 1'
    }

    when:
    observableList.remove(0)
    then:
    observableList.list().with {
      size() == 1 &&
      get(0) == 'old string0 at index 1'
    }

    when:
    observableList.remove('old string0 at index 1')
    then:
    observableList.list().isEmpty()
  }

  def "send event as expected"() {
    given:
    List<ObservableList.Event> events = []
    observableList.eventBus().subscribe {events << it}

    def assertAddEvent = {event, expectedIndex, expectedAdded ->
      event instanceof ObservableList.AddEvent &&
      event.observableList == observableList &&
      event.index == expectedIndex &&
      event.added == expectedAdded
    }
    def assertSetEvent = {event, expectedIndex, expectedOld, expectedNew ->
      event instanceof ObservableList.SetEvent &&
      event.observableList == observableList &&
      event.index == expectedIndex &&
      event.oldElement == expectedOld &&
      event.newElement == expectedNew
    }
    def assertRemoveEvent = {event, expectedIndex, expectedRemoved ->
      event instanceof ObservableList.RemoveEvent &&
      event.observableList == observableList &&
      event.index == expectedIndex &&
      event.removed == expectedRemoved
    }

    expect:
    events.isEmpty()

    when:
    observableList.add('string0')
    then:
    events.size() == 1
    assertAddEvent(events[-1], 0, 'string0')

    when:
    observableList.add(0, 'new string0')
    then:
    events.size() == 2
    assertAddEvent(events[-1], 0, 'new string0')

    when:
    observableList.set(1, 'old string0 at index 1')
    then:
    events.size() == 3
    assertSetEvent(events[-1], 1, 'string0', 'old string0 at index 1')

    when:
    observableList.remove(0)
    then:
    events.size() == 4
    assertRemoveEvent(events[-1], 0, 'new string0')

    when:
    observableList.remove('old string0 at index 1')
    then:
    events.size() == 5
    assertRemoveEvent(events[-1], 0, 'old string0 at index 1')
    observableList.list().isEmpty()
  }

}
