FROM rabbitmq:3.8.18-management

# ENV RABBITMQ_DEFAULT_USER guest
# ENV RABBITMQ_DEFAULT_PASS guest

RUN rabbitmq-plugins enable --offline rabbitmq_management
RUN rabbitmq-plugins enable --offline rabbitmq_mqtt

COPY rabbitmq.conf /etc/rabbitmq
RUN chmod 666 /etc/rabbitmq/*

EXPOSE 15672
EXPOSE 15692
EXPOSE 5672
EXPOSE 1883
