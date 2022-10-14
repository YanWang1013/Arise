package lineage.network.netty;

import static org.jboss.netty.channel.Channels.pipeline;
import lineage.network.codec.Decoder;
import lineage.network.codec.Encoder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public final class CodecFactory implements ChannelPipelineFactory {
	
	private final ProtocolHandler handler;

	public CodecFactory(ProtocolHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
		
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", handler);
		
		return pipeline;
	}
	
}
