package com.aol.simple.react.async.pipes;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.aol.simple.react.async.Queue;
import com.aol.simple.react.stream.traits.EagerFutureStream;

public class EagerPipesTest {
	@Before
	public void setup() {
		Pipes.clear();
	}
	
	@Test
	public void testStream() {
		Queue queue = new Queue();
		queue.add("world");
		queue.close();
		
		assertThat(PipesToEagerStreams.stream(queue).limit(1).toList(),equalTo(Arrays.asList("world")));
	}
	@Test
	public void testStreamIO() {
		Queue queue = new Queue();
		queue.add("world");
		queue.close();
		Pipes.register("hello",queue);
		assertThat(PipesToEagerStreams.streamIOBound(queue).limit(1).toList(),equalTo(Arrays.asList("world")));
	}
	@Test
	public void testStreamCPU() {
		Queue queue = new Queue();
		queue.add("world");
		queue.close();
		Pipes.register("hello",queue);
		assertThat(PipesToEagerStreams.streamCPUBound(queue).limit(1).toList(),equalTo(Arrays.asList("world")));
	}
	@Test
	public void cpuBound() {
		Queue queue = new Queue();
		queue.add("world");
		queue.close();
		EagerFutureStream<String> stream = PipesToEagerStreams.streamCPUBound( queue);
		
		
		assertThat(stream.limit(1).toList(),equalTo(Arrays.asList("world")));
	}
	@Test
	public void ioBound() {
		Queue queue = new Queue();
		queue.add("world");
		queue.close();
		EagerFutureStream<String> stream = PipesToEagerStreams.streamIOBound( queue);
		
		
		assertThat(stream.limit(1).toList(),equalTo(Arrays.asList("world")));
	}
}

