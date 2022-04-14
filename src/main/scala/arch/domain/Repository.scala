package arch.domain

trait Repository[Key, Value] {
  def set(key: Key, value: Value): Unit
  def get(key: Key): Value
}
