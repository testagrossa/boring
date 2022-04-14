package arch.infra.router

import scala.collection.mutable

trait PubSub[Topic, Message] {
  type Callback = Message => Unit
  def publish: (Topic, Message) => Unit
  def subscribe: Topic => Callback => Unit
}

object PubSub {

  class PubSubMock[Topic, Message] extends PubSub[Topic, Message] {

    val callbacksPerTopic =
      mutable.HashMap.empty[Topic, mutable.Set[Callback]]
    val messageQueuePerTopic =
      mutable.HashMap.empty[Topic, mutable.ListBuffer[Message]]

    override def publish = { (topic, message) =>
      messageQueuePerTopic.get(topic) match {
        case Some(queue) => queue.addOne(message)
        case None =>
          messageQueuePerTopic.addOne(
            topic,
            mutable.ListBuffer.empty[Message].addOne(message)
          )
      }

      callbacksPerTopic.get(topic).foreach { callbacks =>
        callbacks.foreach { callback =>
          callback(message)
        }
      }
    }

    override def subscribe = { topic => callback =>
      callbacksPerTopic.get(topic) match {
        case Some(callbacks) => callbacks.addOne(callback)
        case None =>
          callbacksPerTopic.addOne(
            topic,
            mutable.Set.empty[Callback].addOne(callback)
          )
      }

      messageQueuePerTopic.get(topic).foreach { messages =>
        messages.foreach { message =>
          callback(message)
        }
      }
    }
  }

}
