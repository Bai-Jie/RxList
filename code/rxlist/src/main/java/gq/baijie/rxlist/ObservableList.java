package gq.baijie.rxlist;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@NotThreadSafe
public class ObservableList<T> {

  private final Subject<Event<T>, Event<T>> eventBus = PublishSubject.create();

  private List<T> list;

  public static <T> ObservableList<T> create(List<T> list) {
    return new ObservableList<>(list);
  }

  public ObservableList(List<T> list) {
    this.list = list;
  }

  public Observable<Event<T>> eventBus() {
    return eventBus.onBackpressureDrop();
  }

  List<T> list() {
    return Collections.unmodifiableList(list);
  }

  public void add(int index, T element) {
    list.add(index, element);
    eventBus.onNext(new AddEvent<>(this, index, element));
  }

  public void add(T o) {
    add(list.size(), o);
  }

  public void remove(int index) {
    T removed = list.remove(index);
    eventBus.onNext(new RemoveEvent<>(this, index, removed));
  }

  public void remove(T o) {
    int index = list.indexOf(o);
    if (index >= 0) {
      remove(index);
    }
  }

  public void set(int index, T element) {
    T oldElement = list.set(index, element);
    eventBus.onNext(new SetEvent<>(this, index, oldElement, element));
  }

  public static class Event<T> {

    public final ObservableList<T> observableList;

    public Event(ObservableList<T> observableList) {
      this.observableList = observableList;
    }
  }

  public static class AddEvent<T> extends Event<T> {

    public final int index;
    public final T added;

    public AddEvent(ObservableList<T> observableList, int index, T added) {
      super(observableList);
      this.index = index;
      this.added = added;
    }
  }

  public static class RemoveEvent<T> extends Event<T> {

    public final int index;
    public final T removed;

    public RemoveEvent(ObservableList<T> observableList, int index, T removed) {
      super(observableList);
      this.index = index;
      this.removed = removed;
    }
  }

  public static class SetEvent<T> extends Event<T> {

    public final int index;
    public final T oldElement;
    public final T newElement;

    public SetEvent(ObservableList<T> observableList, int index, T oldElement, T newElement) {
      super(observableList);
      this.index = index;
      this.oldElement = oldElement;
      this.newElement = newElement;
    }
  }

}
