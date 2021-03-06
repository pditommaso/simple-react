package com.aol.simple.react.monad;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.aol.cyclops.comprehensions.donotation.typed.Do;
import com.aol.cyclops.lambda.api.AsAnyM;
import com.aol.cyclops.lambda.monads.AnyM;
import com.aol.simple.react.stream.traits.EagerFutureStream;
import com.aol.simple.react.stream.traits.LazyFutureStream;
import com.aol.simple.react.stream.traits.SimpleReactStream;

public class DoTest {

	@Test
	public void doTestLazy(){
		for(int i=0;i<100;i++){
		LazyFutureStream<Integer> result =
				Do.add(LazyFutureStream.of(1,2,3))
												.add(Optional.of(2))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
												
		List<Integer> blocked = result.block();
		
		assertThat(blocked,equalTo(Arrays.asList(3,4,5)));
		}
	}
	public <T> AnyM<T> anyM(SimpleReactStream<T> stream){
		return AsAnyM.notTypeSafeAnyM(stream);
	}
	@Test
	public void doTestSimple(){
		for(int i=0;i<1000;i++){
		SimpleReactStream<Integer> result = Do.add(anyM(SimpleReactStream.of(1,2,3)))
												.add(Optional.of(2))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.block(),equalTo(Arrays.asList(3,4,5)));
		}
	}
	@Test
	public void doTestEager(){
		for(int i=0;i<10;i++){
		EagerFutureStream<Integer> result = Do.add(EagerFutureStream.of(1,2,3))
												.add(Optional.of(2))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.block(),equalTo(Arrays.asList(3,4,5)));
		}
	}
	@Test
	public void doTestEagerOptional(){
		for(int i=0;i<1000;i++){
		Optional<List<Integer>> result = Do.add(lookup("empty"))
												.add(EagerFutureStream.of(1,2,3))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.isPresent(),equalTo(false));
		}
	}
	@Test
	public void doTestLazyOptional(){
		Optional<List<Integer>> result = Do.add(lookup("empty"))
												.add(LazyFutureStream.of(1,2,3))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.isPresent(),equalTo(false));
	}
	@Test
	public void doTestSimpleOptional(){
		Optional<List<Integer>> result = Do.add(lookup("empty"))
												.add(anyM(SimpleReactStream.of(1,2,3)))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.isPresent(),equalTo(false));
	}
	@Test
	public void doTestEagerOptionalEmptyStream(){
		Optional<List<Integer>> result = Do.add(lookup("1"))
												.add(EagerFutureStream.<Integer>of())
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.get().size(),equalTo(0));
	}
	@Test
	public void doTestLazyOptionalEmptyStream(){
		Optional<List<Integer>> result = Do.add(lookup("1"))
												.add(LazyFutureStream.<Integer>of())
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		System.out.println(result);
		assertThat(result.get().size(),equalTo(0));
	}
	@Test
	public void doTestSimpleOptionalEmptyStream(){
		Optional<List<Integer>> result = Do.add(lookup("1"))
												.add(anyM(SimpleReactStream.<Integer>of()))
												.yield((Integer a) -> (Integer b) -> a+b)
												.unwrap();
		assertThat(result.get().size(),equalTo(0));
	}
	
	private Optional<Integer> lookup(String key){
		if("empty".equals(key))
			return Optional.empty();
		else
			return Optional.of(1);
	}
}

