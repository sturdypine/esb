package spc.esb.server.mq;

import javax.annotation.Resource;
import javax.jms.Message;

import org.springframework.jdbc.CannotGetJdbcConnectionException;

import spc.esb.common.service.JournalService;
import spc.esb.constant.ESBCommon;
import spc.esb.constant.ESBConfig;
import spc.esb.data.IMessage;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.config.AppConfig;
import spc.webos.mq.jms.AbstractBytesMessageListener;

public class ESBLogMsgListener extends AbstractBytesMessageListener
{
	protected void onMessage(Message msg, String queue, String corId, byte[] buf)
	{
		try
		{
			if (queue.indexOf(ESBCommon.DEFAULT_ESBLOG) >= 0)
			{ // is esb.log
				String logPoint = msg.getStringProperty(ESBCommon.JMS_LOGPOINT);
				log.info("LogPoint:{}", logPoint);
				IMessage soap = SOAPConverter.getInstance().deserialize(buf);
				try
				{
					journalService.doJournal(soap, logPoint);
				}
				catch (CannotGetJdbcConnectionException e)
				{
					journalService.sendLog(soap, logPoint);
					throw e;
				}
			}
			else
			{ // is esb.alarm
				IMessage soap = SOAPConverter.getInstance().deserialize(buf);
				try
				{
					journalService.doAlarm(soap, buf, null, null);
				}
				catch (CannotGetJdbcConnectionException e)
				{
					journalService.sendAlarm(soap);
					throw e;
				}
			}
		}
		catch (CannotGetJdbcConnectionException e)
		{
			int sleepSeconds = AppConfig.getInstance().getProperty(ESBConfig.JOURNAL_exSleepSeconds,
					30);
			log.warn("db cnn fail:{}, sleep:{}, ex:{}", queue, sleepSeconds, e.toString());
			try
			{
				Thread.sleep(sleepSeconds * 1000);
			}
			catch (InterruptedException e1)
			{
			}
		}
		catch (Throwable t)
		{
			log.warn("onMessage:", t);
		}
	}

	@Resource
	protected JournalService journalService;

	public void setJournalService(JournalService journalService)
	{
		this.journalService = journalService;
	}
}