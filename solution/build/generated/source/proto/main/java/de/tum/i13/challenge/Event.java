// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: challenger.proto

package de.tum.i13.challenge;

/**
 * Protobuf type {@code Challenger.Event}
 */
public final class Event extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:Challenger.Event)
    EventOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Event.newBuilder() to construct.
  private Event(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Event() {
    symbol_ = "";
    securityType_ = 0;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Event();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private Event(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            symbol_ = s;
            break;
          }
          case 16: {
            int rawValue = input.readEnum();

            securityType_ = rawValue;
            break;
          }
          case 29: {

            lastTradePrice_ = input.readFloat();
            break;
          }
          case 34: {
            com.google.protobuf.Timestamp.Builder subBuilder = null;
            if (lastTrade_ != null) {
              subBuilder = lastTrade_.toBuilder();
            }
            lastTrade_ = input.readMessage(com.google.protobuf.Timestamp.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(lastTrade_);
              lastTrade_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return de.tum.i13.challenge.ChallengerProto.internal_static_Challenger_Event_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return de.tum.i13.challenge.ChallengerProto.internal_static_Challenger_Event_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            de.tum.i13.challenge.Event.class, de.tum.i13.challenge.Event.Builder.class);
  }

  public static final int SYMBOL_FIELD_NUMBER = 1;
  private volatile java.lang.Object symbol_;
  /**
   * <code>string symbol = 1;</code>
   * @return The symbol.
   */
  @java.lang.Override
  public java.lang.String getSymbol() {
    java.lang.Object ref = symbol_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      symbol_ = s;
      return s;
    }
  }
  /**
   * <code>string symbol = 1;</code>
   * @return The bytes for symbol.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getSymbolBytes() {
    java.lang.Object ref = symbol_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      symbol_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int SECURITY_TYPE_FIELD_NUMBER = 2;
  private int securityType_;
  /**
   * <code>.Challenger.SecurityType security_type = 2;</code>
   * @return The enum numeric value on the wire for securityType.
   */
  @java.lang.Override public int getSecurityTypeValue() {
    return securityType_;
  }
  /**
   * <code>.Challenger.SecurityType security_type = 2;</code>
   * @return The securityType.
   */
  @java.lang.Override public de.tum.i13.challenge.SecurityType getSecurityType() {
    @SuppressWarnings("deprecation")
    de.tum.i13.challenge.SecurityType result = de.tum.i13.challenge.SecurityType.valueOf(securityType_);
    return result == null ? de.tum.i13.challenge.SecurityType.UNRECOGNIZED : result;
  }

  public static final int LAST_TRADE_PRICE_FIELD_NUMBER = 3;
  private float lastTradePrice_;
  /**
   * <code>float last_trade_price = 3;</code>
   * @return The lastTradePrice.
   */
  @java.lang.Override
  public float getLastTradePrice() {
    return lastTradePrice_;
  }

  public static final int LAST_TRADE_FIELD_NUMBER = 4;
  private com.google.protobuf.Timestamp lastTrade_;
  /**
   * <code>.google.protobuf.Timestamp last_trade = 4;</code>
   * @return Whether the lastTrade field is set.
   */
  @java.lang.Override
  public boolean hasLastTrade() {
    return lastTrade_ != null;
  }
  /**
   * <code>.google.protobuf.Timestamp last_trade = 4;</code>
   * @return The lastTrade.
   */
  @java.lang.Override
  public com.google.protobuf.Timestamp getLastTrade() {
    return lastTrade_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : lastTrade_;
  }
  /**
   * <code>.google.protobuf.Timestamp last_trade = 4;</code>
   */
  @java.lang.Override
  public com.google.protobuf.TimestampOrBuilder getLastTradeOrBuilder() {
    return getLastTrade();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getSymbolBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, symbol_);
    }
    if (securityType_ != de.tum.i13.challenge.SecurityType.Equity.getNumber()) {
      output.writeEnum(2, securityType_);
    }
    if (lastTradePrice_ != 0F) {
      output.writeFloat(3, lastTradePrice_);
    }
    if (lastTrade_ != null) {
      output.writeMessage(4, getLastTrade());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getSymbolBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, symbol_);
    }
    if (securityType_ != de.tum.i13.challenge.SecurityType.Equity.getNumber()) {
      size += com.google.protobuf.CodedOutputStream
        .computeEnumSize(2, securityType_);
    }
    if (lastTradePrice_ != 0F) {
      size += com.google.protobuf.CodedOutputStream
        .computeFloatSize(3, lastTradePrice_);
    }
    if (lastTrade_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(4, getLastTrade());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof de.tum.i13.challenge.Event)) {
      return super.equals(obj);
    }
    de.tum.i13.challenge.Event other = (de.tum.i13.challenge.Event) obj;

    if (!getSymbol()
        .equals(other.getSymbol())) return false;
    if (securityType_ != other.securityType_) return false;
    if (java.lang.Float.floatToIntBits(getLastTradePrice())
        != java.lang.Float.floatToIntBits(
            other.getLastTradePrice())) return false;
    if (hasLastTrade() != other.hasLastTrade()) return false;
    if (hasLastTrade()) {
      if (!getLastTrade()
          .equals(other.getLastTrade())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + SYMBOL_FIELD_NUMBER;
    hash = (53 * hash) + getSymbol().hashCode();
    hash = (37 * hash) + SECURITY_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + securityType_;
    hash = (37 * hash) + LAST_TRADE_PRICE_FIELD_NUMBER;
    hash = (53 * hash) + java.lang.Float.floatToIntBits(
        getLastTradePrice());
    if (hasLastTrade()) {
      hash = (37 * hash) + LAST_TRADE_FIELD_NUMBER;
      hash = (53 * hash) + getLastTrade().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static de.tum.i13.challenge.Event parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static de.tum.i13.challenge.Event parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static de.tum.i13.challenge.Event parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static de.tum.i13.challenge.Event parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static de.tum.i13.challenge.Event parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static de.tum.i13.challenge.Event parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(de.tum.i13.challenge.Event prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code Challenger.Event}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:Challenger.Event)
      de.tum.i13.challenge.EventOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return de.tum.i13.challenge.ChallengerProto.internal_static_Challenger_Event_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return de.tum.i13.challenge.ChallengerProto.internal_static_Challenger_Event_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              de.tum.i13.challenge.Event.class, de.tum.i13.challenge.Event.Builder.class);
    }

    // Construct using de.tum.i13.challenge.Event.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      symbol_ = "";

      securityType_ = 0;

      lastTradePrice_ = 0F;

      if (lastTradeBuilder_ == null) {
        lastTrade_ = null;
      } else {
        lastTrade_ = null;
        lastTradeBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return de.tum.i13.challenge.ChallengerProto.internal_static_Challenger_Event_descriptor;
    }

    @java.lang.Override
    public de.tum.i13.challenge.Event getDefaultInstanceForType() {
      return de.tum.i13.challenge.Event.getDefaultInstance();
    }

    @java.lang.Override
    public de.tum.i13.challenge.Event build() {
      de.tum.i13.challenge.Event result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public de.tum.i13.challenge.Event buildPartial() {
      de.tum.i13.challenge.Event result = new de.tum.i13.challenge.Event(this);
      result.symbol_ = symbol_;
      result.securityType_ = securityType_;
      result.lastTradePrice_ = lastTradePrice_;
      if (lastTradeBuilder_ == null) {
        result.lastTrade_ = lastTrade_;
      } else {
        result.lastTrade_ = lastTradeBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof de.tum.i13.challenge.Event) {
        return mergeFrom((de.tum.i13.challenge.Event)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(de.tum.i13.challenge.Event other) {
      if (other == de.tum.i13.challenge.Event.getDefaultInstance()) return this;
      if (!other.getSymbol().isEmpty()) {
        symbol_ = other.symbol_;
        onChanged();
      }
      if (other.securityType_ != 0) {
        setSecurityTypeValue(other.getSecurityTypeValue());
      }
      if (other.getLastTradePrice() != 0F) {
        setLastTradePrice(other.getLastTradePrice());
      }
      if (other.hasLastTrade()) {
        mergeLastTrade(other.getLastTrade());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      de.tum.i13.challenge.Event parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (de.tum.i13.challenge.Event) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object symbol_ = "";
    /**
     * <code>string symbol = 1;</code>
     * @return The symbol.
     */
    public java.lang.String getSymbol() {
      java.lang.Object ref = symbol_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        symbol_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string symbol = 1;</code>
     * @return The bytes for symbol.
     */
    public com.google.protobuf.ByteString
        getSymbolBytes() {
      java.lang.Object ref = symbol_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        symbol_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string symbol = 1;</code>
     * @param value The symbol to set.
     * @return This builder for chaining.
     */
    public Builder setSymbol(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      symbol_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string symbol = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearSymbol() {
      
      symbol_ = getDefaultInstance().getSymbol();
      onChanged();
      return this;
    }
    /**
     * <code>string symbol = 1;</code>
     * @param value The bytes for symbol to set.
     * @return This builder for chaining.
     */
    public Builder setSymbolBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      symbol_ = value;
      onChanged();
      return this;
    }

    private int securityType_ = 0;
    /**
     * <code>.Challenger.SecurityType security_type = 2;</code>
     * @return The enum numeric value on the wire for securityType.
     */
    @java.lang.Override public int getSecurityTypeValue() {
      return securityType_;
    }
    /**
     * <code>.Challenger.SecurityType security_type = 2;</code>
     * @param value The enum numeric value on the wire for securityType to set.
     * @return This builder for chaining.
     */
    public Builder setSecurityTypeValue(int value) {
      
      securityType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>.Challenger.SecurityType security_type = 2;</code>
     * @return The securityType.
     */
    @java.lang.Override
    public de.tum.i13.challenge.SecurityType getSecurityType() {
      @SuppressWarnings("deprecation")
      de.tum.i13.challenge.SecurityType result = de.tum.i13.challenge.SecurityType.valueOf(securityType_);
      return result == null ? de.tum.i13.challenge.SecurityType.UNRECOGNIZED : result;
    }
    /**
     * <code>.Challenger.SecurityType security_type = 2;</code>
     * @param value The securityType to set.
     * @return This builder for chaining.
     */
    public Builder setSecurityType(de.tum.i13.challenge.SecurityType value) {
      if (value == null) {
        throw new NullPointerException();
      }
      
      securityType_ = value.getNumber();
      onChanged();
      return this;
    }
    /**
     * <code>.Challenger.SecurityType security_type = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSecurityType() {
      
      securityType_ = 0;
      onChanged();
      return this;
    }

    private float lastTradePrice_ ;
    /**
     * <code>float last_trade_price = 3;</code>
     * @return The lastTradePrice.
     */
    @java.lang.Override
    public float getLastTradePrice() {
      return lastTradePrice_;
    }
    /**
     * <code>float last_trade_price = 3;</code>
     * @param value The lastTradePrice to set.
     * @return This builder for chaining.
     */
    public Builder setLastTradePrice(float value) {
      
      lastTradePrice_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>float last_trade_price = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearLastTradePrice() {
      
      lastTradePrice_ = 0F;
      onChanged();
      return this;
    }

    private com.google.protobuf.Timestamp lastTrade_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> lastTradeBuilder_;
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     * @return Whether the lastTrade field is set.
     */
    public boolean hasLastTrade() {
      return lastTradeBuilder_ != null || lastTrade_ != null;
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     * @return The lastTrade.
     */
    public com.google.protobuf.Timestamp getLastTrade() {
      if (lastTradeBuilder_ == null) {
        return lastTrade_ == null ? com.google.protobuf.Timestamp.getDefaultInstance() : lastTrade_;
      } else {
        return lastTradeBuilder_.getMessage();
      }
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public Builder setLastTrade(com.google.protobuf.Timestamp value) {
      if (lastTradeBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        lastTrade_ = value;
        onChanged();
      } else {
        lastTradeBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public Builder setLastTrade(
        com.google.protobuf.Timestamp.Builder builderForValue) {
      if (lastTradeBuilder_ == null) {
        lastTrade_ = builderForValue.build();
        onChanged();
      } else {
        lastTradeBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public Builder mergeLastTrade(com.google.protobuf.Timestamp value) {
      if (lastTradeBuilder_ == null) {
        if (lastTrade_ != null) {
          lastTrade_ =
            com.google.protobuf.Timestamp.newBuilder(lastTrade_).mergeFrom(value).buildPartial();
        } else {
          lastTrade_ = value;
        }
        onChanged();
      } else {
        lastTradeBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public Builder clearLastTrade() {
      if (lastTradeBuilder_ == null) {
        lastTrade_ = null;
        onChanged();
      } else {
        lastTrade_ = null;
        lastTradeBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public com.google.protobuf.Timestamp.Builder getLastTradeBuilder() {
      
      onChanged();
      return getLastTradeFieldBuilder().getBuilder();
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    public com.google.protobuf.TimestampOrBuilder getLastTradeOrBuilder() {
      if (lastTradeBuilder_ != null) {
        return lastTradeBuilder_.getMessageOrBuilder();
      } else {
        return lastTrade_ == null ?
            com.google.protobuf.Timestamp.getDefaultInstance() : lastTrade_;
      }
    }
    /**
     * <code>.google.protobuf.Timestamp last_trade = 4;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder> 
        getLastTradeFieldBuilder() {
      if (lastTradeBuilder_ == null) {
        lastTradeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Timestamp, com.google.protobuf.Timestamp.Builder, com.google.protobuf.TimestampOrBuilder>(
                getLastTrade(),
                getParentForChildren(),
                isClean());
        lastTrade_ = null;
      }
      return lastTradeBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:Challenger.Event)
  }

  // @@protoc_insertion_point(class_scope:Challenger.Event)
  private static final de.tum.i13.challenge.Event DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new de.tum.i13.challenge.Event();
  }

  public static de.tum.i13.challenge.Event getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Event>
      PARSER = new com.google.protobuf.AbstractParser<Event>() {
    @java.lang.Override
    public Event parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new Event(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Event> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Event> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public de.tum.i13.challenge.Event getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

