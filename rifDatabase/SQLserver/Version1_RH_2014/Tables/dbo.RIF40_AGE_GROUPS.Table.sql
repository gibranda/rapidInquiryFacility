USE [RIF40]
GO
/****** Object:  Table [dbo].[RIF40_AGE_GROUPS]    Script Date: 19/09/2014 12:07:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[RIF40_AGE_GROUPS](
	[AGE_GROUP_ID] [numeric](3, 0) NOT NULL,
	[OFFSET] [numeric](3, 0) NOT NULL,
	[LOW_AGE] [numeric](3, 0) NOT NULL,
	[HIGH_AGE] [numeric](3, 0) NOT NULL,
	[FIELDNAME] [varchar](50) NOT NULL,
 CONSTRAINT [RIF40_AGE_GROUPS_PK] PRIMARY KEY CLUSTERED 
(
	[AGE_GROUP_ID] ASC,
	[OFFSET] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[RIF40_AGE_GROUPS]  WITH NOCHECK ADD  CONSTRAINT [RIF40_AGE_GROUP_ID_FK] FOREIGN KEY([AGE_GROUP_ID])
REFERENCES [dbo].[RIF40_AGE_GROUP_NAMES] ([AGE_GROUP_ID])
GO
ALTER TABLE [dbo].[RIF40_AGE_GROUPS] CHECK CONSTRAINT [RIF40_AGE_GROUP_ID_FK]
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.AGE_GROUP_ID' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'COLUMN',@level2name=N'AGE_GROUP_ID'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.OFFSET' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'COLUMN',@level2name=N'OFFSET'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.LOW_AGE' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'COLUMN',@level2name=N'LOW_AGE'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.HIGH_AGE' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'COLUMN',@level2name=N'HIGH_AGE'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.FIELDNAME' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'COLUMN',@level2name=N'FIELDNAME'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_SSMA_SOURCE', @value=N'RIF40.RIF40_AGE_GROUPS.RIF40_AGE_GROUPS_PK' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'RIF40_AGE_GROUPS', @level2type=N'CONSTRAINT',@level2name=N'RIF40_AGE_GROUPS_PK'
GO
